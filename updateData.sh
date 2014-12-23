#!/bin/bash

# Upgrade project_modules xtypes in projects
echo "Upgrade project_modules xtypes in projects"
sed -i 's/<xtype>sitools.user.modules.datasetExplorer/<xtype>sitools.user.modules.DatasetExplorer/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.projectDescription/<xtype>sitools.user.modules.ProjectDescription/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.contentEditorModule/<xtype>sitools.user.modules.ContentEditor/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.datastorageExplorer/<xtype>sitools.user.modules.DataStorageExplorer/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.addToCartModule/<xtype>sitools.user.modules.AddToCartModule/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.sitoolsFitsMain/<xtype>sitools.extension.modules.FitsViewer/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.formsModule/<xtype>sitools.user.modules.FormModule/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.projectServices/<xtype>sitools.user.modules.ProjectService/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.contentViewerModule/<xtype>sitools.user.modules.ContentViewerModule/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.feedsReaderProject/<xtype>sitools.user.modules.FeedsProjectModule/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.projectGraphTree/<xtype>sitools.user.modules.ProjectGraphModule/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.datasetExplorerDataView/<xtype>sitools.user.modules.DatasetExplorerDataview/g' data/projects/map/*.xml
sed -i 's/<xtype>sitools.user.modules.formsAsMenu/<xtype>sitools.user.modules.FormAsMenuModule/g' data/projects/map/*.xml

# Upgrade dataviews jsObject in datasets
echo "Upgrade dataviews jsObject in datasets"
sed -i 's/<jsObject>sitools.user.component.dataviews.livegrid.LiveGrid/<jsObject>sitools.user.component.datasets.dataviews.Livegrid/g' data/datasets/map/*.xml
sed -i 's/<jsObject>sitools.user.component.dataviews.cartoView.cartoView/<jsObject>sitools.user.component.datasets.dataviews.CartoView/g' data/datasets/map/*.xml

# Upgrade gui_services xtypes in plugins_gui_services
echo "Upgrade gui_services xtypes in plugins_gui_services"
sed -i 's/<xtype>sitools.user.component.dataviews.services.columnsDefinitionService/<xtype>sitools.user.component.datasets.services.ColumnsDefinitionService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.sorterService/<xtype>sitools.user.component.datasets.services.SorterService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.sitoolsFitsService/<xtype>sitools.extension.component.datasets.services.fitsService.FitsService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.filterService/<xtype>sitools.user.component.datasets.services.FilterService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.plotService/<xtype>sitools.user.component.datasets.services.PlotService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.WindowImageZoomer/<xtype>sitools.user.component.datasets.services.WindowImageZoomerService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.displaySelectionCartService/<xtype>sitools.user.component.datasets.services.DisplaySelectionCartService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.viewDataDetailsService/<xtype>sitools.user.component.datasets.services.RecordDetailService/g' data/plugins_gui_services/map/*.xml
sed -i 's/<xtype>sitools.user.component.dataviews.services.addToCartService/<xtype>sitools.user.component.datasets.services.AddToCartService/g' data/plugins_gui_services/map/*.xml

# Upgrade form_component xtypes in forms
echo "Upgrade form_component jsAdminObject in forms"
sed -i 's/<jsAdminObject>sitools.admin.forms.oneParam.TextField/<jsAdminObject>sitools.admin.forms.componentsAdminDef.oneParam.TextField/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.oneParam.withValues/<jsAdminObject>sitools.admin.forms.componentsAdminDef.oneParam.WithValues/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.oneParam.withoutValues/<jsAdminObject>sitools.admin.forms.componentsAdminDef.oneParam.BooleanCheckbox/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.oneParam.NumberFieldAdmin/<jsAdminObject>sitools.admin.forms.componentsAdminDef.oneParam.NumberFieldAdmin/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.oneParam.NoValuesWithProperties/<jsAdminObject>sitools.admin.forms.componentsAdminDef.oneParam.NoValuesWithProperties/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.multiParam.coneSearch/<jsAdminObject>sitools.admin.forms.componentsAdminDef.multiParam.ConeSearch/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.oneParam.NumericBetween/<jsAdminObject>sitools.admin.forms.componentsAdminDef.oneParam.NumericBetween/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.noParam.label/<jsAdminObject>sitools.admin.forms.componentsAdminDef.noParam.Label/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.noParam.image/<jsAdminObject>sitools.admin.forms.componentsAdminDef.noParam.Image/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.noParam.hr/<jsAdminObject>sitools.admin.forms.componentsAdminDef.noParam.Hr/g' data/forms/map/*.xml
sed -i 's/<jsAdminObject>sitools.admin.forms.oneParam.DateBetween/<jsAdminObject>sitools.admin.forms.componentsAdminDef.oneParam.DateBetween/g' data/forms/map/*.xml

# Upgrade form_component xtypes in forms
echo "Upgrade form_component jsUserObject in forms"
sed -i 's/<jsUserObject>sitools.common.forms.components.BooleanCheckbox/<jsUserObject>sitools.public.forms.components.BooleanCheckbox/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.CheckBox/<jsUserObject>sitools.public.forms.components.CheckBox/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.ComboBox/<jsUserObject>sitools.public.forms.components.ComboBox/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.ConeSearchCartesien/<jsUserObject>sitools.public.forms.components.ConeSearchCartesien/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.ConeSearchPGSphere/<jsUserObject>sitools.public.forms.components.ConeSearchPGSphere/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.DateBetween/<jsUserObject>sitools.public.forms.components.DateBetween/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.Hl/<jsUserObject>sitools.public.forms.components.Hl/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.Image/<jsUserObject>sitools.public.forms.components.Image/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.Label/<jsUserObject>sitools.public.forms.components.Label/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.ListBox/<jsUserObject>sitools.public.forms.components.ListBox/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.ListBoxMultiple/<jsUserObject>sitools.public.forms.components.ListBoxMultiple/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.NumberField/<jsUserObject>sitools.public.forms.components.NumberField/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.NumericBetween/<jsUserObject>sitools.public.forms.components.NumericBetween/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.OneOrBetween/<jsUserObject>sitools.public.forms.components.OneOrBetween/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.Radio/<jsUserObject>sitools.public.forms.components.Radio/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.TextField/<jsUserObject>sitools.public.forms.components.TextField/g' data/forms/map/*.xml
sed -i 's/<jsUserObject>sitools.common.forms.components.mapPanel/<jsUserObject>sitools.public.forms.components.MapPanel/g' data/forms/map/*.xml

# Clear dependencies tags from xml files
echo "Clear dependencies tags from xml files"
sed -i '/<dependencies>/,/<\/dependencies>/d' data/plugins_gui_services/map/*.xml
sed -i '/<dependencies>/,/<\/dependencies>/d' data/datasets_views/map/*.xml
sed -i '/<dependencies>/,/<\/dependencies>/d' data/gui_services/map/*.xml
sed -i '/<dependencies>/,/<\/dependencies>/d' data/projects_modules/map/*.xml

echo "[OK]"