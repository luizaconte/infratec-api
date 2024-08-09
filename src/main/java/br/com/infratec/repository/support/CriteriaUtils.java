package br.com.infratec.repository.support;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public class CriteriaUtils {

    public static <T> Path<T> getPath(final Root<?> root, final String attributeName) {
        final String[] paths = attributeName.split("\\.");

        if (paths.length == 1) {
            return root.get(attributeName);
        }

        Join<?, ?> lastJoin = root.getJoins().stream()
                .filter(j -> j.getAttribute().getName().equals(paths[0]))
                .findFirst()
                .orElseGet(() -> root.join(paths[0], JoinType.LEFT));

        for (int i = 1; i < paths.length - 1; i++) {
            lastJoin = getLastJoin(lastJoin, paths[i]);
        }

        return lastJoin.get(paths[paths.length - 1]);
    }

    private static Join<?, ?> getLastJoin(final Join<?, ?> join, final String pathName) {
        for (final Join<?, ?> j : join.getJoins()) {
            final var attribute = j.getAttribute();
            if (attribute.getName().equals(pathName)) {
                return j;
            }
        }
        return join.join(pathName, JoinType.LEFT);
    }

}
