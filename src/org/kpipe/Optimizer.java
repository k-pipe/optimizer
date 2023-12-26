package org.kpipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Optimizer {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Optimizer(args).optimize();
    }

    private static final String DELETED_EXTENSION = ".delete";
    private final String[] command;

    private int deleted;
    private int kept;

    private final List<Path> files = new ArrayList<>();

    public Optimizer(String[] args) {
        this.command = args;
    }

    public void optimize() throws IOException, InterruptedException {
        Files.walk(Path.of(".")).filter(p -> p.toFile().isFile()).forEach((files::add));
        System.out.println("Read "+files.size()+" files");
        if (commandFails()) {
            error("Could not execute test command (before removing any files)");
        }
        System.out.println("Test with all files succeeded");
        checkFilesNecessary();
    }

    private boolean commandFails() throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).start();
        process.waitFor();
        return process.exitValue() != 0;
    }

    private void checkFilesNecessary() {
        try {
            tryCheckFileNecessary(0,files.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            error("Exception occurred: "+e.getMessage());
        }
    }


    private void tryCheckFileNecessary(int from, int to) throws IOException, InterruptedException {
        int percent = ((kept+deleted)*100)/files.size();
        System.out.print(percent+"% done ("+deleted+" deleted, "+kept+" kept): ");
        System.out.print("testing "+from+".."+(to-1)+": ");
        System.out.flush();
        List<Path> list = files.subList(from, to);
        for (Path p : list) {
            Files.move(p, renamed(p));
        }
        if (commandFails()) {
            System.out.print("failed");
            if (list.size() == 1) {
                Path p = list.get(0);
                Files.move(renamed(p), p);
                System.out.println("--> keeping "+p);
                kept++;
            } else {
                for (Path p : list) {
                    Files.move(renamed(p), p);
                }
                int mid = (from+to)/2;
                System.out.println("--> splitting "+from+"-.."+mid+"..."+to);
                tryCheckFileNecessary(from, mid);
                tryCheckFileNecessary(mid, to);
            }
        } else {
            System.out.print("success");
            for (Path p : list) {
                Files.delete(renamed(p));
                deleted++;
            }
            System.out.println("--> deleted "+list.get(0)+" to "+list.get(list.size()-1));
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