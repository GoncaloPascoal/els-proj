package pt.up.fe.els2022.instructions.text;

import org.apache.commons.collections4.map.ListOrderedMap;
import pt.up.fe.els2022.adapters.Interval;
import pt.up.fe.els2022.utils.CollectionUtils;
import pt.up.fe.specs.util.SpecsCollections;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TextInstructionFactory {
    private static Interval parseInterval(Object obj) {
        int start;
        Integer end = null;

        if (obj instanceof List<?>) {
            try {
                List<Integer> list = SpecsCollections.cast((List<?>) obj, Integer.class);
                if (list.size() != 2 || list.get(0) == null) {
                    throw new IllegalArgumentException("Interval list must have two integers and the first cannot be null.");
                }
                start = list.get(0);
                end = list.get(1);
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException("Interval list must be composed of integers.");
            }
        }
        else if (obj instanceof Integer) {
            start = (Integer) obj;
        }
        else {
            throw new IllegalArgumentException("Interval must be an integer or list of integers.");
        }

        return new Interval(start, end);
    }

    private static Map<String, Interval> parseColumnIntervals(Object obj) {
        Map<String, Object> objMap = CollectionUtils.castMap((Map<?, ?>) obj, String.class, Object.class);

        Map<String, Interval> result = new ListOrderedMap<>();
        objMap.forEach((k, v) -> result.put(k, parseInterval(v)));

        return result;
    }

    private static List<Interval> parseIntervalList(Object obj) {
        List<Object> objList = SpecsCollections.cast((List<?>) obj, Object.class);
        return objList.stream().map(TextInstructionFactory::parseInterval).collect(Collectors.toList());
    }

    public static TextInstruction createInstruction(String type, Map<String, Object> args) {
        switch (type) {
            case "columnInterval": {
                Object linesObj = args.get("lines");
                Object columnIntervalsObj = args.get("columnIntervals");
                Object stripWhitespaceObj = args.get("stripWhitespace");

                if (linesObj == null || columnIntervalsObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for columnInterval instruction.");
                }

                if (!(linesObj instanceof List<?> && columnIntervalsObj instanceof Map<?, ?>
                        && (stripWhitespaceObj == null || stripWhitespaceObj instanceof Boolean))) {
                    throw new IllegalArgumentException("Incorrect argument types for columnInterval instruction.");
                }

                try {
                    List<Interval> lines = parseIntervalList(linesObj);
                    Map<String, Interval> columnIntervals = parseColumnIntervals(columnIntervalsObj);
                    Boolean stripWhitespace = (Boolean) stripWhitespaceObj;

                    return new ColumnIntervalInstruction(lines, columnIntervals, stripWhitespace);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for columnInterval instruction.");
                }
            }
            case "regexLineDelimiter": {
                Object patternsObj = args.get("patterns");
                Object delimiterObj = args.get("delimiter");

                if (patternsObj == null || delimiterObj == null) {
                    throw new IllegalArgumentException("Missing required arguments for regexLineDelimiter instruction.");
                }

                if (!(patternsObj instanceof List<?> && delimiterObj instanceof String)) {
                    throw new IllegalArgumentException("Incorrect argument types for regexLineDelimiter instruction.");
                }

                try {
                    List<String> patterns = SpecsCollections.cast((List<?>) patternsObj, String.class);
                    String delimiter = (String) delimiterObj;

                    return new RegexLineDelimiterInstruction(patterns, delimiter);
                }
                catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Incorrect argument types for columnInterval instruction.");
                }
            }
            default:
                throw new IllegalArgumentException(type + " is not a valid text instruction type.");
        }
    }
}
