package bursa.utils;

import org.hashids.Hashids;

import java.util.Objects;

public class CryptoTool {
    private final Hashids hashids;

    public CryptoTool(String salt) {
        int minHashLength = 10;
        this.hashids = new Hashids(salt, minHashLength);
    }

    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if (Objects.nonNull(res) && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
