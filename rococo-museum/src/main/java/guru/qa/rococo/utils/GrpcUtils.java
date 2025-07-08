package guru.qa.rococo.utils;

import org.springframework.data.domain.Sort;

import java.util.List;

public class GrpcUtils {

    public static Sort createSortFromList(List<String> sortList) {
        if (sortList.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = sortList.stream()
                .map(sortStr -> {
                    if (sortStr.startsWith("-")) {
                        return Sort.Order.desc(sortStr.substring(1));
                    } else {
                        return Sort.Order.asc(sortStr);
                    }
                })
                .toList();

        return Sort.by(orders);
    }
}
