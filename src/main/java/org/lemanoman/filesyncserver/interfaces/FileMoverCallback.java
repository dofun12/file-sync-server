package org.lemanoman.filesyncserver.interfaces;

import org.lemanoman.filesyncserver.dto.FileOperationDto;

public interface FileMoverCallback {
    void onStart(String id, FileOperationDto fileOperation);
    void onFinish(String id, FileOperationDto fileOperation, String status);
}
