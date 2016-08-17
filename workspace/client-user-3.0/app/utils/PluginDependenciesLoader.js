/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*global Ext*/

/**
 * Utility class to load all modules class to produce the list of files in order to create a minify version of sitools2 client
 * 
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.utils.PluginDependenciesLoader', {
        
    requires : [
        //modules
        'sitools.user.modules.AddToCartModule',
        'sitools.user.modules.ContentEditor',
        'sitools.user.modules.ContentViewerModule',
        'sitools.user.modules.DatasetExplorer',
        'sitools.user.modules.DataStorageExplorer',
        'sitools.user.modules.FeedsProjectModule',
        'sitools.user.modules.FormAsMenuModule',
        'sitools.user.modules.FormModule',
        'sitools.user.modules.ProjectDescription',
        'sitools.user.modules.ProjectService',
        //gui services
        'sitools.user.component.datasets.services.AddToCartService',
        'sitools.user.component.datasets.services.ColumnsDefinitionService',
        'sitools.user.component.datasets.services.DisplaySelectionCartService',
        'sitools.user.component.datasets.services.FilterService',
        'sitools.user.component.datasets.services.PlotService',
        'sitools.user.component.datasets.services.RecordDetailService',
        'sitools.user.component.datasets.services.SorterService',
        'sitools.user.component.datasets.services.WindowImageZoomerService',
        ////form components
        'sitools.public.forms.components.AbstractConeSearch',
        'sitools.public.forms.components.BooleanCheckbox',
        'sitools.public.forms.components.CheckBox',
        'sitools.public.forms.components.ComboBox',
        'sitools.public.forms.components.ConeSearchCartesien',
        'sitools.public.forms.components.ConeSearchPGSphere',
        'sitools.public.forms.components.DateBetween',
        'sitools.public.forms.components.Hl',
        'sitools.public.forms.components.Image',
        'sitools.public.forms.components.Label',
        'sitools.public.forms.components.ListBoxMultiple',
        'sitools.public.forms.components.ListBox',
        'sitools.public.forms.components.MapPanel',
        'sitools.public.forms.components.NumberField',
        'sitools.public.forms.components.NumericBetween',
        'sitools.public.forms.components.OneOrBetween',
        'sitools.public.forms.components.Radio',
        'sitools.public.forms.components.TextField',
        ////dataset views
        'sitools.user.component.datasets.dataviews.CartoView',
        'sitools.user.component.datasets.dataviews.Livegrid'
    ]
});