package fraud.api.v1.velocity
/**
 * Aggregation function type for velocity metrics calculation
 */
enum Filter implements Serializable {
    AnyGreaterThan {
        boolean apply(List<String> data, String value) {
            double targetValue = Double.parseDouble(value)
            data.find {
                try {
                    double currentValue = Double.parseDouble(it)
                    return currentValue > targetValue
                } catch (NumberFormatException ignored) {
                    return false
                }
            }
        }
    },
    AllGreaterThan {
        boolean apply(List<String> data, String value) {
            double targetValue = Double.parseDouble(value)
            !data.find {
                try {
                    double currentValue = Double.parseDouble(it)
                    return currentValue <= targetValue
                } catch (NumberFormatException ignored) {
                    return true
                }
            }
        }
    },
    AnyLessThan {
        boolean apply(List<String> data, String value) {
            double targetValue = Double.parseDouble(value)
            data.find {
                try {
                    double currentValue = Double.parseDouble(it)
                    return currentValue < targetValue
                } catch (NumberFormatException ignored) {
                    return false
                }
            }
        }
    },
    AllLessThan {
        boolean apply(List<String> data, String value) {
            double targetValue = Double.parseDouble(value)
            !data.find {
                try {
                    double currentValue = Double.parseDouble(it)
                    return currentValue >= targetValue
                } catch (NumberFormatException ignored) {
                    return true
                }
            }
        }
    },
    AnyEqualTo {
        boolean apply(List<String> data, String value) {
            data.find { value == it}
        }
    },
    AllEqualTo {
        boolean apply(List<String> data, String value) {
            !data.find { value != it}
        }
    }

    abstract boolean apply(List<String> data, String value)
}
