package org.lemanoman;

import java.io.File;

public record HashResult(
        File file, String hash, String hashType, long size, long timeTaken
) {
}
