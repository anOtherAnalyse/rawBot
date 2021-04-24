package rawbot.utils;

import java.io.IOException;
import java.lang.System;
import java.util.zip.Inflater;
import java.util.zip.Deflater;
import java.util.zip.DataFormatException;

import rawbot.connection.ReadBuffer;
import rawbot.connection.WriteBuffer;

public class DeflateUtil {

  private int compressionThreshold;
  private Deflater deflater;
  private Inflater inflater;

  // Deflation tmp buffer
  private final byte[] tmp_buffer = new byte[8192];

  public DeflateUtil(int compressionThreshold) {
    this.compressionThreshold = compressionThreshold;
    this.deflater = new Deflater();
    this.inflater = new Inflater();
  }

  /* Compress (zip) input data */
  public byte[] deflate(byte[] buff) {
    WriteBuffer output_buff = new WriteBuffer();

    if(buff.length < this.compressionThreshold) {
      output_buff.writeVarInt(0);
      output_buff.writeBytes(buff, buff.length);
      return output_buff.getBuff();
    } else {
      output_buff.writeVarInt(buff.length);

      this.deflater.setInput(buff, 0, buff.length);
      this.deflater.finish();

      while(!this.deflater.finished()) {
        int deflated = this.deflater.deflate(this.tmp_buffer);
        if(deflated > 0) {
          output_buff.writeBytes(this.tmp_buffer, deflated);
        } else if(this.deflater.needsInput()) break;
      }
      this.deflater.reset();

      return output_buff.getBuff();
    }
  }

  /* Decompress (unzip) input data */
  public byte[] inflate(byte[] buff) throws IOException {
    ReadBuffer input_buff = new ReadBuffer(buff);
    int length = input_buff.readVarInt();

    if(length == 0) {

      byte[] out = new byte[buff.length - input_buff.getCursor()];
      System.arraycopy(buff, input_buff.getCursor(), out, 0, out.length);
      return out;

    } else if(length >= this.compressionThreshold && length <= 2097152) {
      this.inflater.setInput(buff, input_buff.getCursor(), buff.length - input_buff.getCursor());
      byte[] out = new byte[length];

      try {
        this.inflater.inflate(out, 0, length);
      } catch (DataFormatException e) {
        throw new IOException("Invalid compression format: " + e.getMessage());
      } finally {
        this.inflater.reset();
      }

      return out;
    } else {
      throw new IOException(String.format("Compressed data wrong size (%d bytes), max: 2097152, min: %d", length, this.compressionThreshold));
    }
  }
}
