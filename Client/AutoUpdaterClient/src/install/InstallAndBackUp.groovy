package install

import java.awt.Toolkit
import java.io.File

import javax.swing.JDialog
import javax.swing.JOptionPane

import updater.Updater

import groovy.io.FileType

/** Groovy class which handles files opertation - copies files,
 get files number in specified catalog */
class InstallAndBackUp extends Observable implements Runnable {
	/** file count in specified catalog */
	private int file_count
	/** id of actualization which files are currently install */
	def actualization_id
	/** directory of downloaded files */
	def source_path
	/** directory of program to update */
	def dest_path
	/** direcotry for backup files */
	def backup_path

	/**
	 * Notifies all observers
	 *
	 * @param message
	 *            message to send
	 */
	private void setMessage(Message message) {
		setChanged()
		notifyObservers(message)
	}

	/**
	 * Begins installation - copies files
	 */
	@Override
	public void run() {
		file_count = 0

		try {
			def result = copy_dir(source_path, dest_path, backup_path)
			if(!result) {
				setMessage(new Message("Installation failed.", true, false, false));
				setMessage(new Message("Installation failed.", actualization_id));
			}

			setMessage(new Message("Complete", true, false, false));
		} catch(IOException e) {
			setMessage(new Message("Installation failed.", actualization_id));
		}
	}

	/**
	 * Copies directory from one place to another
	 * @param source_path	directory path
	 * @param dest_path		destination path
	 * @param backup_path	backup path or null if no backup
	 * @return				true if successful, false if failed
	 * @throws IOException	if an I/O error occurs
	 */
	def boolean copy_dir(String source_path, String dest_path, String backup_path)
	throws IOException {

		boolean result = false
		String temp_fileName = ""
		String temp_sourceName = ""
		String temp_destName = ""
		String temp_backUpName = ""
		def source = new File(source_path)
		def destination = new File(dest_path)
		def backup = backup_path != null ? new File(backup_path) : null
		// if source is not directory then return false
		if(!source.isDirectory()) {
			return false
		}

		// if destination does not exist, then creates it
		if(!destination.exists()) {
			result = destination.mkdirs()
			if(!result) {
				return false
			}
		}

		// iterate over all files in specified directory
		source.eachFile(FileType.FILES) {
			temp_fileName = dest_path + File.separator + it.getName()
			temp_backUpName = backup_path != null ? backup_path + File.separator + it.getName() : null
			def tempfile = new File(temp_fileName)

			// try to copy file to backup directory if not null 
			if(temp_backUpName != null && tempfile.exists()) {
				if(!backup.exists()) {
					result = backup.mkdirs()
					if(!result) {
						return false
					}
				}

				setMessage(new Message("Backup to: " + temp_backUpName, true, false, false));
				copy_file(temp_fileName, temp_backUpName)
			}

			// try to copy file to destination directory
			setMessage(new Message("Copy to: " + temp_fileName, true, false, false));
			copy_file(it.getAbsolutePath(), temp_fileName)

			setMessage(new Message(Integer.toString(++file_count), false, false,
					true));
		}

		// iterate over all directories and run copy_dir recursively
		source.eachDir {
			temp_destName = dest_path + File.separator + it.getName()
			temp_sourceName = source_path + File.separator + it.getName()
			temp_backUpName = backup_path != null ? backup_path + File.separator + it.getName() : null

			copy_dir(temp_sourceName, temp_destName, temp_backUpName)
		}

		return true
	}

	/**
	 * Copies files from one location to another
	 * 
	 * @param source_path	source location
	 * @param dest_path		destination location
	 * @throws IOException  if an I/O error occurs
	 */
	def copy_file(String source_path, String dest_path) throws IOException {
		def source = new File(source_path)
		def destination = new File(dest_path)

		source.withInputStream { is ->
			destination.setText("");
			destination << is
			is.close();
		}

		destination.createNewFile()
	}

	/**
	 * Gets number of files in specified catalog
	 * @param source_path	catalog location
	 * @return	files number or -1 if error
	 */
	def int getFileNumber(String source_path) {
		def source = new File(source_path)
		def count = 0

		if(!source.isDirectory() || !source.exists()) {
			return -1
		}

		source.traverse(type : FileType.FILES) { it -> ++count }

		return count
	}
}