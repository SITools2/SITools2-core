package fr.cnes.sitools.resources.healpix;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MocResourceModel extends ResourceModel {

    public static final String GEOMETRY_COLUMN = "geometryColumn";

    /**
     * JeoSearchResourceModel constructor
     */
    public MocResourceModel() {
        super();
        setClassAuthor("AKKA Tecnologies");
        setClassOwner("CNES");
        setClassVersion("0.1");
        setName("MocResourceModel");
        setDescription("Generate MOC from dataset");
        setResourceClassName(MocResource.class.getName());


        ResourceParameter param1 = new ResourceParameter(GEOMETRY_COLUMN, "The name of column containing the geometry information",
                ResourceParameterType.PARAMETER_INTERN);
        param1.setValueType("xs:dataset.columnAlias");
        this.addParam(param1);

        this.setApplicationClassName(DataSetApplication.class.getName());
        this.setDataSetSelection(DataSetSelectionType.ALL);
        this.getParameterByName("methods").setValue("GET");
        this.getParameterByName("url").setValue("/moc");

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
                if (value.equals("")) {
                    ConstraintViolation constraint = new ConstraintViolation();
                    constraint.setMessage("There is not column defined");
                    constraint.setLevel(ConstraintViolationLevel.CRITICAL);
                    constraint.setValueName(param.getName());
                    constraints.add(constraint);
                }
                return constraints;
            }
        };
    }

}
