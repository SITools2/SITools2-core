package fr.cnes.sitools.service.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.service.storage.model.StorageDirectory;

/**
 * DataStorageStoreXmlMapLockable
 * 
 * @author tx.chevallier
 */
public class DataStorageStoreXmlMapLockable extends DataStorageStoreXmlMap {

  /** file to lock */
  private File fileToLock;

  /**
   * DataStorageStoreXmlMapLockable
   * 
   * @param location
   *          File
   * @param context
   *          Context
   */
  public DataStorageStoreXmlMapLockable(File location, Context context) {
    super(location, context);
  }

  @Override
  public void init(File location) {
    super.init(location);
    this.fileToLock = new File(location.getAbsolutePath() + File.pathSeparator + "lock.dat");
    try {
      FileUtils.touch(this.fileToLock);
    }
    catch (IOException e) {
      getLog().log(Level.WARNING, null, e);
    }

  }

  @Override
  public StorageDirectory create(StorageDirectory resource) {

    FileLock lock = null;
    FileChannel channel = null;
    
    synchronized (fileToLock) {
      try {
        channel = new RandomAccessFile(fileToLock, "rw").getChannel();

        lock = channel.lock();

        return super.create(resource);

      }
      catch (FileNotFoundException e) {
        getLog().log(Level.WARNING, null, e);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      finally {
        try {
          if (lock != null) {
            lock.release();
          }
          if (channel != null) {
            channel.close();
          }
        }
        catch (IOException e) {
          getLog().log(Level.WARNING, null, e);
        }
      }
    }

    return null;

  }

  @Override
  public List<StorageDirectory> getList(ResourceCollectionFilter filter) {

    FileLock lock = null;
    FileChannel channel = null;
    synchronized (fileToLock) {
      try {
        channel = new RandomAccessFile(fileToLock, "rw").getChannel();

        lock = channel.lock(0L, Long.MAX_VALUE, true);

        return super.getList(filter);

      }
      catch (FileNotFoundException e) {
        getLog().log(Level.WARNING, null, e);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      finally {
        try {
          if (lock != null) {
            lock.release();
          }
          if (channel != null) {
            channel.close();
          }
        }
        catch (IOException e) {
          getLog().log(Level.WARNING, null, e);
        }
      }
    }

    return null;

  }

  @Override
  public StorageDirectory retrieve(String id) {

    FileLock lock = null;
    FileChannel channel = null;

    synchronized (fileToLock) {

      try {
        channel = new RandomAccessFile(fileToLock, "rw").getChannel();

        lock = channel.lock(0L, Long.MAX_VALUE, true);
        return super.retrieve(id);

      }
      catch (FileNotFoundException e) {
        getLog().log(Level.WARNING, null, e);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      finally {
        try {
          if (lock != null) {
            lock.release();
          }
          if (channel != null) {
            channel.close();
          }
        }
        catch (IOException e) {
          getLog().log(Level.WARNING, null, e);
        }
      }
    }

    return null;

  }

  @Override
  public boolean delete(String id) {

    FileLock lock = null;
    FileChannel channel = null;
    synchronized (fileToLock) {
      try {
        channel = new RandomAccessFile(fileToLock, "rw").getChannel();

        lock = channel.lock();

        return super.delete(id);

      }
      catch (FileNotFoundException e) {
        getLog().log(Level.WARNING, null, e);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      finally {
        try {
          if (lock != null) {
            lock.release();
          }
          if (channel != null) {
            channel.close();
          }
        }
        catch (IOException e) {
          getLog().log(Level.WARNING, null, e);
        }
      }
    }
    return super.delete(id);

  }

  @Override
  public StorageDirectory update(StorageDirectory o) {

    FileLock lock = null;
    FileChannel channel = null;
    synchronized (fileToLock) {
      try {
        channel = new RandomAccessFile(fileToLock, "rw").getChannel();

        lock = channel.lock();

        return super.update(o);

      }
      catch (FileNotFoundException e) {
        getLog().log(Level.WARNING, null, e);
      }
      catch (IOException e) {
        getLog().log(Level.WARNING, null, e);
      }
      finally {
        try {
          if (lock != null) {
            lock.release();
          }
          if (channel != null) {
            channel.close();
          }
        }
        catch (IOException e) {
          getLog().log(Level.WARNING, null, e);
        }
      }
    }
    return null;
  }

}
