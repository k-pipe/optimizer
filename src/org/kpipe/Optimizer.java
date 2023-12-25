package org.kpipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Optimizer {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Optimizer(args).optimize();
    }

    private static final String DELETED_EXTENSION = ".delete";
    private final String[] command;

    public Optimizer(String[] args) {
        this.command = args;
    }

    public void optimize() throws IOException, InterruptedException {
        if (commandFails()) {
            error("Could not execute test command (before removing any files)");
        }
        System.out.println("Test with all files succeeded");
        Files.walk(Path.of(".")).forEach(this::checkFileNecessary);
    }

    private boolean commandFails() throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).start();
        process.waitFor();
        return process.exitValue() != 0;
    }

    private void checkFileNecessary(Path path) {
        try {
            if (path.toFile().isFile()) {
                tryCheckFileNecessary(path);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            error("Exception occurred for file "+path+": "+e.getMessage());
        }
    }


    private void tryCheckFileNecessary(Path path) throws IOException, InterruptedException {
        System.out.print(path+": ");
        System.out.flush();
        Path renamed = renamed(path);
        Files.move(path, renamed);
        if (commandFails()) {
            System.out.println("kept");
            Files.move(renamed, path);
        } else {
            System.out.println("removed");
            Files.delete(renamed);
        }
    }

    private Path renamed(Path path) {
        return path.getParent().resolve(path.getFileName()+DELETED_EXTENSION);
    }

    private void error(String message) {
        System.err.println(message);
        System.exit(1);
    }

}