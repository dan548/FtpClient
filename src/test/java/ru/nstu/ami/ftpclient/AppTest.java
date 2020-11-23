package ru.nstu.ami.ftpclient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Objects;

import static org.junit.Assert.assertTrue;

public class AppTest {

    private FakeFtpServer fakeFtpServer;

    private FtpClient ftpClient;

    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new FileEntry("/data/foobar.txt", "abcdef 1234567890"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }

    @After
    public void teardown() throws IOException {
        ftpClient.close();
        fakeFtpServer.stop();
    }

    @Test
    public void givenRemoteFile_whenListingRemoteFiles_thenItIsContainedInList() throws IOException {
        Collection<String> files = ftpClient.listFiles("");
        assertTrue(files.contains("foobar.txt"));
    }

    @Test
    public void givenLocalFile_whenUploadingIt_thenItExistsOnRemoteLocation()
            throws URISyntaxException, IOException {

        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("baz.txt")).toURI());
        ftpClient.putFileToPath(file, "/buz.txt");
        assertTrue(fakeFtpServer.getFileSystem().exists("/buz.txt"));
    }

    @Test
    public void givenRemoteFile_whenDownloading_thenItIsOnTheLocalFilesystem() throws IOException {
        ftpClient.downloadFile("/buz.txt", "downloaded_buz.txt");
        assertTrue(new File("downloaded_buz.txt").exists());
        new File("downloaded_buz.txt").delete(); // cleanup
    }

    @Test
    public void givenRemoteFile_whenRenamingIt_thenItIsRenamed() throws IOException{
        assertTrue(ftpClient.renameFile("foobar.txt", "renamed_foobar.txt"));
        assertTrue(fakeFtpServer.getFileSystem().exists("/data/renamed_foobar.txt"));
    }
}
