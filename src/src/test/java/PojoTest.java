import code.EmployeePojo;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Test;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PojoTest {

    @Test
    void pojoWithReflection() {
        List<String> propertyNames = Arrays.stream(PropertyUtils.getPropertyDescriptors(EmployeePojo.class))
                        .map(PropertyDescriptor::getDisplayName)
                        .collect(Collectors.toList());
        assertThat(propertyNames).containsExactly("start");
    }
}
