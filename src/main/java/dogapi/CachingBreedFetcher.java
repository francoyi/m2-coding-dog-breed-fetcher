package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private int callsMade = 0;
    private final Map<String, List<String>> cache = new HashMap<>();

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = Objects.requireNonNull(fetcher, "fetcher must not be null");
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = breed == null ? null : breed.toLowerCase();

        if (key != null && cache.containsKey(key)) {
            return new ArrayList<>(cache.get(key));
        }

        callsMade++;
        try {
            List<String> result = fetcher.getSubBreeds(breed);

            List<String> immutableCopy = Collections.unmodifiableList(new ArrayList<>(result));
            if (key != null) {
                cache.put(key, immutableCopy);
            }

            return new ArrayList<>(immutableCopy);
        } catch (BreedNotFoundException e) {
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}