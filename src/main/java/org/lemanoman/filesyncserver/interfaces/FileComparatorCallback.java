package org.lemanoman.filesyncserver.interfaces;

import org.lemanoman.filesyncserver.dto.FileOperationDto;

import java.util.List;

public interface FileComparatorCallback {
    void onFinish(List<FileOperationDto> fileOperations);
}
