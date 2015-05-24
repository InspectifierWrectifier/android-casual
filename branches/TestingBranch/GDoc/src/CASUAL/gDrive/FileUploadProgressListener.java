/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.gDrive;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

import java.io.IOException;
/**
 *
 * @author loglud
 */
public class FileUploadProgressListener implements MediaHttpUploaderProgressListener {
        public downUpProgress dlp;
  public FileUploadProgressListener (String name) {
     super();
     dlp = new downUpProgress(name,true);
  }
  @Override
  public void progressChanged(MediaHttpUploader uploader) throws IOException {
    switch (uploader.getUploadState()) {
      case INITIATION_STARTED:
        break;
      case INITIATION_COMPLETE:
        break;
      case MEDIA_IN_PROGRESS:
        dlp.updateProgress((int) (uploader.getProgress() * 100));
        break;
      case MEDIA_COMPLETE:
        dlp.updateProgress((int) 100);
        break;
    }
  }
}

