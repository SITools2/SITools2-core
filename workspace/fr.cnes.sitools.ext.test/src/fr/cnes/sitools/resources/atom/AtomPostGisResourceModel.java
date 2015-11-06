package fr.cnes.sitools.resources.atom;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.util.Util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AtomPostGisResourceModel extends ResourceModel {


    public static final String GEOMETRY_COLUMN = "geometryColumn";
    public static final String QUICKLOOK_COLUMN = "quicklookColumn";
    public static final String THUMBNAIL_COLUMN = "thumbnailColumn";
    public static final String DOWNLOAD_COLUMN = "downloadColumn";
    public static final String MIME_TYPE_COLUMN = "mimeTypeColumn";

    //ATOM PARAMS
    public static final String TITLE_COLUMN = "titleColumn";
    public static final String DESCRIPTION_COLUMN = "descriptionColumn";
    public static final String PUBLISHED_COLUMN = "publishedColumn";
    public static final String UPDATED_COLUMN = "updatedColumn";

    /**
     * JeoSearchResourceModel constructor
     */
    public AtomPostGisResourceModel() {
        super();
        setClassAuthor("AKKA Tecnologies");
        setClassOwner("CNES");
        setClassVersion("0.1");
        setName("AtomPostGisResourceModel");
        setDescription("ATOM export on the fly on postgis dataset without dictionary mapping");
        setResourceClassName(AtomPostGisResource.class.getName());

        ResourceParameter param1 = new ResourceParameter(GEOMETRY_COLUMN, "The name of column containing the geometry information",
                ResourceParameterType.PARAMETER_INTERN);
        param1.setValueType("xs:dataset.columnAlias");
        this.addParam(param1);

        ResourceParameter paramQuicklook = new ResourceParameter(QUICKLOOK_COLUMN, "The name of column containing the quicklook information",
                ResourceParameterType.PARAMETER_INTERN);
        paramQuicklook.setValueType("xs:dataset.columnAlias");
        this.addParam(paramQuicklook);

        ResourceParameter paramThumbnail = new ResourceParameter(THUMBNAIL_COLUMN, "The name of column containing the thumbnail information",
                ResourceParameterType.PARAMETER_INTERN);
        paramThumbnail.setValueType("xs:dataset.columnAlias");
        this.addParam(paramThumbnail);

        ResourceParameter paramDownload = new ResourceParameter(DOWNLOAD_COLUMN, "The name of column containing the download information",
                ResourceParameterType.PARAMETER_INTERN);
        paramDownload.setValueType("xs:dataset.columnAlias");
        this.addParam(paramDownload);

        ResourceParameter paramMimeType = new ResourceParameter(MIME_TYPE_COLUMN, "The mimeType of the data downloaded",
                ResourceParameterType.PARAMETER_INTERN);
        paramMimeType.setValueType("xs:dataset.columnAlias");
        this.addParam(paramMimeType);

        ResourceParameter paramTitle = new ResourceParameter(TITLE_COLUMN, "The name of the column containing the title",
                ResourceParameterType.PARAMETER_INTERN);
        paramTitle.setValueType("xs:dataset.columnAlias");
        this.addParam(paramTitle);

        ResourceParameter paramDescription = new ResourceParameter(DESCRIPTION_COLUMN, "The name of the column containing the description",
                ResourceParameterType.PARAMETER_INTERN);
        paramDescription.setValueType("xs:dataset.columnAlias");
        this.addParam(paramDescription);

        ResourceParameter paramPublished = new ResourceParameter(PUBLISHED_COLUMN, "The name of the column containing the published information",
                ResourceParameterType.PARAMETER_INTERN);
        paramPublished.setValueType("xs:dataset.columnAlias");
        this.addParam(paramPublished);

        ResourceParameter paramUpdated = new ResourceParameter(UPDATED_COLUMN, "The name of the column containing the updated information",
                ResourceParameterType.PARAMETER_INTERN);
        paramUpdated.setValueType("xs:dataset.columnAlias");
        this.addParam(paramUpdated);

        this.setApplicationClassName(DataSetApplication.class.getName());
        this.setDataSetSelection(DataSetSelectionType.MULTIPLE);
        this.getParameterByName("methods").setValue("GET");
        this.getParameterByName("url").setValue("/geojson/search");

    }

    @Override
    public Validator<ResourceModel> getValidator() {
        return new Validator<ResourceModel>() {

            @Override
            public Set<ConstraintViolation> validate(ResourceModel item) {
                Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
                Map<String, ResourceParameter> params = item.getParametersMap();
                ResourceParameter param = params.get(GEOMETRY_COLUMN);
                String value = param.getValue();
                if (Util.isEmpty(value)) {
                    ConstraintViolation constraint = new ConstraintViolation();
                    constraint.setMessage("There is not column defined for geometry");
                    constraint.setLevel(ConstraintViolationLevel.CRITICAL);
                    constraint.setValueName(param.getName());
                    constraints.add(constraint);
                }

                //Check that there is a mimeType if download column is defined
                ResourceParameter paramDownload = params.get(DOWNLOAD_COLUMN);
                if (!Util.isEmpty(paramDownload.getValue())) {
                    ResourceParameter paramMimeType = params.get(MIME_TYPE_COLUMN);
                    if (Util.isEmpty(paramMimeType.getValue())) {
                        ConstraintViolation constraint = new ConstraintViolation();
                        constraint.setMessage("MimeType is mandatory if download is configured");
                        constraint.setLevel(ConstraintViolationLevel.CRITICAL);
                        constraint.setValueName(paramMimeType.getName());
                        constraints.add(constraint);
                    }
                }
                return constraints;
            }
        };
    }
}
