package com.khartec.waltz.service.physical_specification_definition;

import com.khartec.waltz.data.physical_specification_definition.PhysicalSpecDefinitionDao;
import com.khartec.waltz.data.physical_specification_definition.PhysicalSpecDefinitionFieldDao;
import com.khartec.waltz.data.physical_specification_definition.PhysicalSpecDefinitionSampleFileDao;
import com.khartec.waltz.model.EntityKind;
import com.khartec.waltz.model.EntityReference;
import com.khartec.waltz.model.Operation;
import com.khartec.waltz.model.ReleaseLifecycleStatus;
import com.khartec.waltz.model.changelog.ImmutableChangeLog;
import com.khartec.waltz.model.physical_specification_definition.ImmutablePhysicalSpecDefinition;
import com.khartec.waltz.model.physical_specification_definition.PhysicalSpecDefinition;
import com.khartec.waltz.model.physical_specification_definition.PhysicalSpecDefinitionChangeCommand;
import com.khartec.waltz.service.changelog.ChangeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.khartec.waltz.common.Checks.checkNotNull;
import static com.khartec.waltz.model.EntityReference.mkRef;

@Service
public class PhysicalSpecDefinitionService {

    private final ChangeLogService changeLogService;

    private final PhysicalSpecDefinitionDao physicalSpecDefinitionDao;
    private final PhysicalSpecDefinitionFieldDao physicalSpecDefinitionFieldDao;
    private final PhysicalSpecDefinitionSampleFileDao physicalSpecDefinitionSampleFileDao;


    @Autowired
    public PhysicalSpecDefinitionService(ChangeLogService changeLogService,
                                         PhysicalSpecDefinitionDao physicalSpecDefinitionDao,
                                         PhysicalSpecDefinitionFieldDao physicalSpecDefinitionFieldDao,
                                         PhysicalSpecDefinitionSampleFileDao physicalSpecDefinitionSampleFileDao) {
        checkNotNull(changeLogService, "changeLogService cannot be null");
        checkNotNull(physicalSpecDefinitionDao, "physicalSpecDefinitionDao cannot be null");
        checkNotNull(physicalSpecDefinitionFieldDao, "physicalSpecDefinitionFieldDao cannot be null");
        checkNotNull(physicalSpecDefinitionSampleFileDao, "physicalSpecDefinitionSampleFileDao cannot be null");

        this.changeLogService = changeLogService;
        this.physicalSpecDefinitionDao = physicalSpecDefinitionDao;
        this.physicalSpecDefinitionFieldDao = physicalSpecDefinitionFieldDao;
        this.physicalSpecDefinitionSampleFileDao = physicalSpecDefinitionSampleFileDao;
    }


    public long create(String userName,
                       long specificationId,
                       PhysicalSpecDefinitionChangeCommand command) {

        checkNotNull(userName, "userName cannot be null");
        checkNotNull(command, "command cannot be null");

        long defId = physicalSpecDefinitionDao.create(
                ImmutablePhysicalSpecDefinition.builder()
                        .specificationId(specificationId)
                        .version(command.version())
                        .status(ReleaseLifecycleStatus.DRAFT)
                        .delimiter(command.delimiter())
                        .type(command.type())
                        .provenance("waltz")
                        .createdBy(userName)
                        .lastUpdatedBy(userName)
                        .build());

        changeLogService.write(
                ImmutableChangeLog.builder()
                        .operation(Operation.ADD)
                        .userId(userName)
                        .parentReference(mkRef(EntityKind.PHYSICAL_SPECIFICATION, specificationId))
                        .message("Spec Definition Id: " + defId + " added")
                        .build());

        return defId;
    }


    public int delete(String userName, long specDefinitionId) {

        checkNotNull(userName, "userName cannot be null");

        PhysicalSpecDefinition specDefinition = physicalSpecDefinitionDao.getById(specDefinitionId);

        checkNotNull(specDefinition, "specDefinition cannot be null");

        int defDelCount = physicalSpecDefinitionDao.delete(specDefinitionId);
        int fieldDelCount = physicalSpecDefinitionFieldDao.deleteForSpecDefinition(specDefinitionId);
        int fileDelCount = physicalSpecDefinitionSampleFileDao.deleteForSpecDefinition(specDefinitionId);

        changeLogService.write(
                ImmutableChangeLog.builder()
                        .operation(Operation.REMOVE)
                        .userId(userName)
                        .parentReference(mkRef(EntityKind.PHYSICAL_SPECIFICATION, specDefinition.specificationId()))
                        .message("Spec Definition Id: " + specDefinitionId + " removed")
                        .build());

        return defDelCount + fieldDelCount + fileDelCount;
    }


    public List<PhysicalSpecDefinition> findForSpecification(long specificationId) {
        return physicalSpecDefinitionDao.findForSpecification(specificationId);
    }
}
