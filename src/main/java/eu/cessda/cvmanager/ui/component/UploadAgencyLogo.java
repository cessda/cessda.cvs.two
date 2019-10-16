package eu.cessda.cvmanager.ui.component;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.gesis.wts.service.dto.AgencyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.viritin.label.MLabel;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

public class UploadAgencyLogo extends CustomComponent {
	private static final String HARDCODED_TMP_UPLOADS_DIRECTORY = "/tmp/uploads";
	private static final long serialVersionUID = 5884964436829655205L;
	private static final Logger log = LoggerFactory.getLogger(UploadAgencyLogo.class);

	public void init(AgencyDTO agencyDTO) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		// Show uploaded file in this placeholder
		final MLabel imagePreview = new MLabel().withContentMode(ContentMode.HTML);

		// Implement both receiver that saves upload in a file and
		// listener for successful upload
		class ImageReceiver implements Receiver, SucceededListener {

			private static final long serialVersionUID = -1923790134165706057L;
			private File file;

			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				// Create upload stream
				FileOutputStream fos = null; // Stream to write to
				try {
					// Open the file for writing.
					file = new File(HARDCODED_TMP_UPLOADS_DIRECTORY + "/" + filename);
					fos = new FileOutputStream(file);
				} catch (final java.io.FileNotFoundException e) {
					new Notification("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE)
							.show(Page.getCurrent());
					return null;
				}
				return fos; // Return the output stream to write to
			}

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				// Show the uploaded file in the image viewer

				try {

					java.awt.Image image = ImageIO.read(file);

					BufferedImage bi = this.createResizedCopy(image, 240, 100, false);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bi, "png", baos);

					String data = DatatypeConverter.printBase64Binary(baos.toByteArray());
					String imageString = "data:image/png;base64," + data;
					String htmlImage = "<img src='" + imageString + "'>";

					imagePreview.setValue(htmlImage);
					agencyDTO.setLogo(imageString);

				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}

			}

			BufferedImage createResizedCopy(java.awt.Image originalImage, int scaledWidth, int scaledHeight,
					boolean preserveAlpha) {
				Dimension imgSize = new Dimension(originalImage.getWidth(null), originalImage.getHeight(null));
				Dimension boundary = new Dimension(scaledWidth, scaledHeight);

				Dimension newDimension = getScaledDimension(imgSize, boundary);

				int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
				BufferedImage scaledBI = new BufferedImage(newDimension.width, newDimension.height, imageType);
				Graphics2D g = scaledBI.createGraphics();
				if (preserveAlpha) {
					g.setComposite(AlphaComposite.Src);
				}
				g.drawImage(originalImage, 0, 0, newDimension.width, newDimension.height, null);
				g.dispose();
				return scaledBI;
			}

			private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

				int originalWidth = imgSize.width;
				int originalHeight = imgSize.height;
				int boundWidth = boundary.width;
				int boundHeight = boundary.height;
				int newWidth = originalWidth;
				int newHeight = originalHeight;

				// first check if we need to scale width
				if (originalWidth > boundWidth) {
					// scale width to fit
					newWidth = boundWidth;
					// scale height to maintain aspect ratio
					newHeight = (newWidth * originalHeight) / originalWidth;
				}

				// then check if we need to scale even with the new height
				if (newHeight > boundHeight) {
					// scale height to fit instead
					newHeight = boundHeight;
					// scale width to maintain aspect ratio
					newWidth = (newHeight * originalWidth) / originalHeight;
				}

				return new Dimension(newWidth, newHeight);
			}
		}

		ImageReceiver receiver = new ImageReceiver();

		// Create the upload with a caption and set receiver later
		final Upload upload = new Upload("Choose file to add/replace agency logo (max 5MB)", receiver);

		upload.addSucceededListener(receiver);

		// Prevent too big downloads
		final long UPLOAD_LIMIT = 5000000l;
		upload.addStartedListener(new StartedListener() {
			private static final long serialVersionUID = 4728847902678459488L;

			@Override
			public void uploadStarted(StartedEvent event) {
				if (event.getContentLength() > UPLOAD_LIMIT) {
					Notification.show("Too big file", Notification.Type.ERROR_MESSAGE);
					upload.interruptUpload();
				}
			}
		});

		// Check the size also during progress
		upload.addProgressListener(new ProgressListener() {
			private static final long serialVersionUID = 8587352676703174995L;

			@Override
			public void updateProgress(long readBytes, long contentLength) {
				if (readBytes > UPLOAD_LIMIT) {
					Notification.show("Too big file", Notification.Type.ERROR_MESSAGE);
					upload.interruptUpload();
				}
			}
		});
		if (agencyDTO.getLogo() != null) {
			imagePreview.setValue("<img src='" + agencyDTO.getLogo() + "'>");
		}

		// Put the components in a panel
		VerticalLayout panelContent = new VerticalLayout();
		panelContent.setMargin(false);
		panelContent.addComponents(upload, imagePreview);
		// END-EXAMPLE: component.upload.basic

		// Create uploads directory
		File uploads = new File(HARDCODED_TMP_UPLOADS_DIRECTORY);
		if (!uploads.exists() && !uploads.mkdir())
			layout.addComponent(new Label("ERROR: Could not create upload dir"));

		layout.addComponent(panelContent);

		setCompositionRoot(layout);
		setStyleName("upload-logo");
	}
}