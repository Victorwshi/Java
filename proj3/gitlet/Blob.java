package gitlet;

/** Blob class for blob object.
 * @author Victor Shi
 */
public class Blob extends GitObject {
    /** The BLOB of gitlet.*/
    private byte[] blob;
    /** The BLOBHASH of blob.*/
    private String blobHash;

    /** A BLOB object taking in BBLOBS.*/
    public Blob(byte[] bblobs) {
        blob = bblobs;
        blobHash = Utils.sha1(blob);
    }
    /** To get BLOB.
     * @return get blobs
     * */
    public byte[] getBlob() {
        return blob;
    }

    /** To get BLOBHASH.
     * @return get blobhash
     * */
    public String getBlobHash() {
        return blobHash;
    }

}
