package com.example.report.util;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Abstraction over "upload to S3 / file server" so FileGenerationService doesn't
 * care whether files end up on local disk or in S3. Swap the active implementation
 * via the `report.storage.mode` property (local | s3).
 */
public interface FileStorageService {

    /**
     * Uploads/stores the given local file and returns a publicly (or pre-signed)
     * resolvable download URL for it.
     */
    String store(Path localFilePath, String objectKey) throws IOException;
}
