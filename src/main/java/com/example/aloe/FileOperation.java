package com.example.aloe;

import java.io.File;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

class FileOperation {
    enum OperationType {
        COPY, MOVE;
    }

    private static final Queue<FileOperation> queue = new LinkedList<>();
    private OperationType operationType;
    private File source;
    private File destination;

    public OperationType getOperationType() {
        return operationType;
    }

    public File getSource() {
        return source;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public void setDestination(File destination) {
        this.destination = destination;
    }

    public File getDestination() {
        return destination;
    }

    public FileOperation(OperationType operationType, File source, File destination) {
        this.operationType = operationType;
        this.source = source;
        this.destination = destination;
    }

    public static boolean addOperationToQueue(FileOperation operation) {
        for (FileOperation o : queue) {
            if (o.equals(operation)) {
                return false;
            }
        }
        queue.add(operation);
        return true;
    }

    public static void removeOperationFromQueue(FileOperation operation) {
        queue.remove(operation);
    }

    public static void clearOperationFromQueue() {
        queue.clear();
    }

    public static boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileOperation that = (FileOperation) o;
        return operationType == that.operationType && Objects.equals(source, that.source) && Objects.equals(destination, that.destination);
    }
}