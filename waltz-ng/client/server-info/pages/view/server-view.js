/*
 * Waltz - Enterprise Architecture
 * Copyright (C) 2016, 2017  Waltz open source project
 * See README.md for more information
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import { initialiseData } from "../../../common";
import { CORE_API } from '../../../common/services/core-api-utils';
import { dynamicSections } from "../../../dynamic-section/dynamic-section-definitions";

import template from "./server-view.html";


const bindings = {
};


const initialState = {
    serverInfo: null,

    appsSection: dynamicSections.appsSection,
    bookmarksSection: dynamicSections.bookmarksSection,
    entityNamedNotesSection: dynamicSections.entityNamedNotesSection,
    changeLogSection: dynamicSections.changeLogSection
};


const addToHistory = (historyStore, server) => {
    if (! server) { return; }
    historyStore.put(
        server.hostname,
        'SERVER',
        'main.server.view',
        { id: server.id });
};


function controller($stateParams, historyStore, serviceBroker) {
    const vm = initialiseData(this, initialState);

    vm.$onInit = () => {
        vm.serverId = $stateParams.id;
        vm.parentEntityRef = {
            kind: "SERVER",
            id: vm.serverId
        };

        serviceBroker
            .loadViewData(CORE_API.ServerInfoStore.getById, [vm.parentEntityRef.id])
            .then(r => {
                vm.serverInfo = r.data;
                addToHistory(historyStore, vm.serverInfo);
            });
    };
}


controller.$inject = [
    "$stateParams",
    "HistoryStore",
    "ServiceBroker"
];


const component = {
    template,
    bindings,
    controller
};


export default {
    component,
    id: "waltzServerView"
};
