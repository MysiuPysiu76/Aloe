package com.example.aloe.archive;

import java.io.File;

interface Archive {
    void compress(ArchiveParameters parameters);

    void decompress(File file);
}
