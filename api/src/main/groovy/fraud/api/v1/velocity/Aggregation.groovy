package fraud.api.v1.velocity
/**
 * Aggregation function type for velocity metrics calculation
 */
enum Aggregation implements Serializable {
    Max{
        Double apply(List<String> data) {
            Double max = Double.MIN_VALUE
            data.each {
                try {
                    double value = Double.parseDouble(it)
                    if (value > max) {
                        max = value
                    }
                } catch (NumberFormatException ignored) {
                    return null
                }
            }
            return max
        }
    },
    Min{
        Double apply(List<String> data) {
            Double min = Double.MAX_VALUE
            data.each {
                try {
                    double value = Double.parseDouble(it)
                    if (value < min) {
                        min = value
                    }
                } catch (NumberFormatException ignored) {
                    return null
                }
            }
            return min
        }
    },
    Avg{
        Double apply(List<String> data) {
            Double sum = 0.0
            if (data.size() > 0) {
                data.each {
                    try {
                        sum += Double.parseDouble(it)
                    } catch (NumberFormatException ignored) {
                        return null
                    }
                }
                return sum / data.size()
            } else {
                return 0.0
            }
        }
    },
    Sum{
        Double apply(List<String> data) {
            Double sum = 0.0
            data.each {
                try {
                    sum += Double.parseDouble(it)
                } catch (NumberFormatException ignored) {
                    return null
                }
            }
            return sum
        }
    },
    Count{
        Double apply(List<String> data) {
            return (double) data.size()
        }
    },
    UniqueCount{
        Double apply(List<String> data) {
            return (double) data.toSet().size()
        }
    }

    abstract Double apply(List<String> data)
}
