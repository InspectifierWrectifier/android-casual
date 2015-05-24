/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.gDrive;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;

/**
 *
 * @author loglud
 */
public class FileDownloadProgressListener implements MediaHttpDownloaderProgressListener {
    public downUpProgress dlp;
  public FileDownloadProgressListener (String name) {
     super();
     dlp = new downUpProgress(name,true);
  }

  @Override
  public void progressChanged(MediaHttpDownloader downloader) {
    switch (downloader.getDownloadState()) {
      case MEDIA_IN_PROGRESS:
          dlp.updateProgress((int) (downloader.getProgress() * 100));
        //View.header2("Download is in progress: " + downloader.getProgress());
        break;
      case MEDIA_COMPLETE:
        dlp.updateProgress((int) 100);
        break;
    }
  }
}
