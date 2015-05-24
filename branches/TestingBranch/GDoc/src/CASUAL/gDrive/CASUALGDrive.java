/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.gDrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.base.Preconditions;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mortbay.util.ajax.JSON;
/**
 *
 * @author loglud
 */
public class CASUALGDrive {
    
    
    private static final String UPLOAD_FILE_PATH = "/Users/loglud/Documents/About Stacks.pdf";
    private static final String DIR_FOR_DOWNLOADS = "/Users/loglud/Documents/2/";
    private static final java.io.File UPLOAD_FILE = new java.io.File(UPLOAD_FILE_PATH);

    
    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    /**
     * Global Drive API client.
     */
    public static Drive drive;
    Credential credential;
    
    public CASUALGDrive() throws Exception
    {
        credential = authorize();
        drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                        "CASUAL/Beta").build();
    }
    /**
     * Authorizes the installed application to access user's protected data.
     */
    public static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, CASUALGDrive.class.getResourceAsStream("./client_secrets.json"));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
                    + "into drive-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up file credential store
        FileCredentialStore credentialStore = new FileCredentialStore(new java.io.File(
                System.getProperty("user.home"), ".credentials/drive.json"), JSON_FACTORY);
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Arrays.asList("https://www.googleapis.com/auth/drive")).setCredentialStore(credentialStore).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) {
        //Preconditions.checkArgument(!UPLOAD_FILE_PATH.startsWith("Enter ")
          //      && !DIR_FOR_DOWNLOADS.startsWith("Enter "),
            //    "Please enter the upload file path and download directory in %s", CASUALGDrive.class);

        try {
            try {
                // authorization
                Credential credential = authorize();
                // set up the global Drive instance
                drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                        "CASUAL/Beta").build();

                // run commands

//        View.header1("Starting Resumable Media Upload");
//        File uploadedFile = uploadFile(false);
//
//        View.header1("Updating Uploaded File Name");
//        File updatedFile = updateFileWithTestSuffix(uploadedFile.getId());
//
//        View.header1("Starting Resumable Media Download");
//        downloadFile(false, updatedFile);
//
//        View.header1("Starting Simple Media Upload");
//        uploadedFile = uploadFile(true);
//
//        View.header1("Starting Simple Media Download");
                //File downloadfile = drive.files().get("0B_-74RRbTwOqZzJONVlBQ21kd2s").execute();
                //System.out.println(downloadfile.getOriginalFilename());
          //downloadFile(true, downloadfile, DIR_FOR_DOWNLOADS);
//
//        View.header1("Success!");
//        return;
                //FileList results = drive.files().list().setQ("title= 'drivetest-About Stacks.pdf'").execute();
                //System.out.println(results.toPrettyString());
                //System.out.println(results.getItems().get(0).getTitle());
                getAllFileNames();

                
                
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    /**
     * Uploads a file using either resumable or direct media upload.
     */
    public static File uploadFile(boolean useDirectUpload, java.io.File uploadFile) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setTitle(uploadFile.getName());
        InputStreamContent mediaContent = new InputStreamContent("doc/pdf", new BufferedInputStream(
                new FileInputStream(uploadFile)));
        mediaContent.setLength(uploadFile.length());

        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(useDirectUpload);
        //uploader.setProgressListener(new FileUploadProgressListener());
        return insert.execute();
    }
    
    public static List<File> getAllFilesFromDrive() throws IOException
    {
        List<File> result = new ArrayList<File>();
        Files.List request = drive.files().list();

                do {
                    try {
                        FileList files = request.execute();

                        result.addAll(files.getItems());
                        request.setPageToken(files.getNextPageToken());
                    } catch (IOException e) {
                        System.out.println("An error occurred: " + e);
                        request.setPageToken(null);
                    }
                } while (request.getPageToken() != null
                        && request.getPageToken().length() > 0);
                
                return result;
    }

    public static List<String> getAllFileNames() throws IOException {
        List<String> names = new ArrayList<String>();
        List<File> filelist = getAllFilesFromDrive();
        for (File file : filelist) {
            names.add(file.getTitle());
            System.out.println(file.getTitle());
        }
        return names;
        
    }
    
    public static File getFileQuery(String query) throws IOException  
    {
        FileList out = drive.files().list().setQ(query).execute();
        return out.getItems().get(0);
        
    }
    /**
     * Updates the name of the uploaded file to have a "drivetest-" prefix.
     */
    public static File updateFileWithTestSuffix(String id, java.io.File file) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setTitle("drivetest-" + file.getName());
        Drive.Files.Update update = drive.files().update(id, fileMetadata);
        return update.execute();
    }

    /**
     * Downloads a file using either resumable or direct media download.
     */
    public static void downloadFile(boolean useDirectDownload, File downloadFile, 
            String fileLoc)
            throws IOException {
        // create parent directory (if necessary)
        java.io.File parentDir = new java.io.File(fileLoc);
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Unable to create parent directory");
        }
        OutputStream out = new FileOutputStream(new java.io.File(parentDir, downloadFile.getTitle()));

        Drive.Files.Get get = drive.files().get(downloadFile.getId());
        MediaHttpDownloader downloader = get.getMediaHttpDownloader();
        downloader.setDirectDownloadEnabled(useDirectDownload);
        downloader.setProgressListener(new FileDownloadProgressListener(downloadFile.getTitle()));
        downloader.download(new GenericUrl(downloadFile.getDownloadUrl()), out);
    }
    
    
}