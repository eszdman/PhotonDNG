package com.particlesdevs.photoncamera.processing.opengl;

import com.pngencoder.PngEncoder;
import com.pngencoder.PngEncoderBufferedImageConverter;
import util.Log.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class GLImage implements AutoCloseable {
    private static String TAG = "GLImage";
    Point size;
    ByteBuffer byteBuffer;
    GLFormat glFormat;

    public GLImage(BufferedImage image){
        this.size = new Point(image.getWidth(),image.getHeight());
        glFormat = new GLFormat(GLFormat.DataType.SIMPLE_8,image.getColorModel().getPixelSize()/8);
        byteBuffer = getByteBuffer(image);
    }
    public GLImage(File inputFile) {
        try {
            BufferedImage image = ImageIO.read(inputFile);
            this.size = new Point(image.getWidth(),image.getHeight());
            glFormat = new GLFormat(GLFormat.DataType.SIMPLE_8,image.getColorModel().getPixelSize()/8);
            byteBuffer = getByteBuffer(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GLImage(Point size, GLFormat glFormat) {
        this.size = new Point(size);
        this.glFormat = new GLFormat(glFormat);
        byteBuffer = ByteBuffer.allocateDirect(size.x*size.y*glFormat.mChannels*glFormat.mFormat.mSize);
    }

    public GLImage(Point size, GLFormat glFormat, ByteBuffer byteBuffer) {
        this.size = new Point(size);
        this.glFormat = new GLFormat(glFormat);
        this.byteBuffer = byteBuffer;
    }

    private ByteBuffer getByteBuffer(BufferedImage bufferedImage){
        DataBuffer dataBuffer = bufferedImage.getRaster().getDataBuffer();
        ByteBuffer byteBuffer = null;
        if (dataBuffer instanceof DataBufferByte) {
            byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
            byteBuffer = ByteBuffer.wrap(pixelData);
        }
        else if (dataBuffer instanceof DataBufferUShort) {
            short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        }
        else if (dataBuffer instanceof DataBufferShort) {
            short[] pixelData = ((DataBufferShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        }
        else if (dataBuffer instanceof DataBufferInt) {
            int[] pixelData = ((DataBufferInt) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 4);
            byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
        }
        else {
            Log.e("GlImage", "Not implemented for data buffer type: " + dataBuffer.getClass());
        }
        return byteBuffer;
    }

    public BufferedImage getBufferedImage(){
        byteBuffer.position(0);
        IntBuffer buffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int[] arr = new int[Math.max(buffer.remaining(),buffer.capacity())];
        buffer.get(arr);
        return PngEncoderBufferedImageConverter.createFromIntArgb(arr, size.x, size.y);
    }

    public void save(File output){
        BufferedImage bufferedImage = getBufferedImage();
        new PngEncoder()
                .withBufferedImage(bufferedImage)
                .toFile(output);
    }


    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     *
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     *
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     *
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     */
    @Override
    public void close() {
        byteBuffer.clear();

    }
}
