package fr.cnes.sitools.dataset;

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
import fr.cnes.sitools.dataset.model.DataSet;

public class DataSetStoreXMLLockable extends DataSetStoreXMLMap {
  
  

  /** file to lock */
  private File fileToLock;



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
  public DataSet create(DataSet resource) {

    FileLock lock = null;
    FileChannel channel = null;

    try {
      channel = new RandomAccessFile(fileToLock, "rw").getChannel();
      synchronized (fileToLock) {
        lock = channel.lock();
      }
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

    return null;

  }

  public DataSetStoreXMLLockable(File location, Context context) {
    super(location, context);
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<DataSet> getList(ResourceCollectionFilter filter) {

    FileLock lock = null;
    FileChannel channel = null;

    try {
      channel = new RandomAccessFile(fileToLock, "rw").getChannel();
      synchronized (fileToLock) {
        lock = channel.lock();
      }
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

    return null;

  }

  @Override
  public DataSet retrieve(String id) {

    FileLock lock = null;
    FileChannel channel = null;

    try {
      channel = new RandomAccessFile(fileToLock, "rw").getChannel();
      synchronized (fileToLock) {
        lock = channel.lock();
      }
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

    return null;

  }

  @Override
  public boolean delete(String id) {

    FileLock lock = null;
    FileChannel channel = null;

    try {
      channel = new RandomAccessFile(fileToLock, "rw").getChannel();
      synchronized (fileToLock) {
        lock = channel.lock();
      }
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

    return super.delete(id);

  }

  @Override
  public DataSet update(DataSet o) {

    FileLock lock = null;
    FileChannel channel = null;

    try {
      channel = new RandomAccessFile(fileToLock, "rw").getChannel();
      synchronized (fileToLock) {
        lock = channel.lock();
      }
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
    return null;
  }

  

}
