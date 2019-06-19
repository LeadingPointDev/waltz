/*
 * Waltz - Enterprise Architecture
 * Copyright (C) 2016, 2017, 2018, 2019 Waltz open source project
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

import template from "./assessment-rating-summary-pies.html";
import {initialiseData} from "../../../common";
import {color} from "d3-color";


const bindings = {
    summaries: "<"
};


const initialState = {
    summaries: [],
    config: {
        labelProvider: d => d.rating.name,
        valueProvider: d => d.count,
        colorProvider: d => color(d.data.rating.color)
    }
};


function controller() {

    const vm = initialiseData(this, initialState);

    vm.$onInit = () => {
        console.log("init", vm.summaries);
    };

}

controller.$inject = [];


const component = {
    template,
    controller,
    bindings
};


export default {
    id: "waltzAssessmentRatingSummaryPies",
    component
}