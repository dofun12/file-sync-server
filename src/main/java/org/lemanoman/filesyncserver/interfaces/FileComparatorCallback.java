package org.lemanoman.filesyncserver.interfaces;

import org.lemanoman.filesyncserver.dto.FileOperationDto;

import java.util.List;

public interface FileComparatorCallback {
    void onNextCompare(FileOperationDto fileOperation);
    void onFinish(List<FileOperationDto> fileOperations);
}
