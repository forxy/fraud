package fraud.api.v1.check

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
abstract class Entity implements Serializable {
    Map<String, Object> attributes = new HashMap<String, Object>();

    public void putAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public void addAttributeListValue(final String name, final Object value) {
        Object listValue = attributes.get(name);
        if (listValue != null) {
            if (listValue instanceof Collection) {
                ((Collection) listValue).add(value);
            }
        } else {
            List<Object> list = new ArrayList<>();
            list.add(value);
            attributes.put(name, list);
        }
    }
}
