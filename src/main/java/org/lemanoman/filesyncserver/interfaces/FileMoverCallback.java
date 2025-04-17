package org.lemanoman.filesyncserver.interfaces;

import org.lemanoman.filesyncserver.dto.FileOperationDto;

public interface FileMoverCallback {
    void onFinish(String id, FileOperationDto fileOperation, String status);
}
