/*
 * Copyright (C) 2016, 2017 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.example.mymovies.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import com.example.mymovies.R;
import com.example.mymovies.dialogs.filemanager.FileManagerConfig;
import com.example.mymovies.dialogs.filemanager.FileManagerDialog;

public class StorageSettingsFragment extends PreferenceFragmentCompat
{
    @SuppressWarnings("unused")
    private static final String TAG = StorageSettingsFragment.class.getSimpleName();

    private static final String TAG_DIR_CHOOSER_BIND_PREF = "dir_chooser_bind_pref";

    private static final int DOWNLOAD_DIR_CHOOSE_REQUEST = 1;

    /* Preference that is associated with the current dir selection dialog */
    private String dirChooserBindPref;

    public static StorageSettingsFragment newInstance()
    {
        StorageSettingsFragment fragment = new StorageSettingsFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            dirChooserBindPref = savedInstanceState.getString(TAG_DIR_CHOOSER_BIND_PREF);
        final SharedPreferences pref = SettingsManager.getPreferences(getActivity());
        String keySaveTorrentsIn = getString(R.string.pref_key_save_torrents_in);
        Preference saveTorrentsIn = findPreference(keySaveTorrentsIn);
        saveTorrentsIn.setSummary(pref.getString(keySaveTorrentsIn, SettingsManager.Default.saveTorrentsIn));
        saveTorrentsIn.setOnPreferenceClickListener((Preference preference) -> {
            dirChooserBindPref = getString(R.string.pref_key_save_torrents_in);
            dirChooseDialog(pref.getString(dirChooserBindPref, SettingsManager.Default.saveTorrentsIn));

            return true;
        });

        String keySaveTorrentFiles = getString(R.string.pref_key_save_torrent_files);
        SwitchPreferenceCompat saveTorrentFiles = (SwitchPreferenceCompat)findPreference(keySaveTorrentFiles);
        saveTorrentFiles.setChecked(pref.getBoolean(keySaveTorrentFiles,
                                                    SettingsManager.Default.saveTorrentFiles));

        String keySaveTorrentFilesIn = getString(R.string.pref_key_save_torrent_files_in);
        Preference saveTorrentFilesIn = findPreference(keySaveTorrentFilesIn);
        saveTorrentFilesIn.setSummary(pref.getString(keySaveTorrentFilesIn,
                                                     SettingsManager.Default.saveTorrentFilesIn));
        saveTorrentFilesIn.setOnPreferenceClickListener((Preference preference) -> {
            dirChooserBindPref = getString(R.string.pref_key_save_torrent_files_in);
            dirChooseDialog(pref.getString(dirChooserBindPref, SettingsManager.Default.saveTorrentFilesIn));

            return true;
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(TAG_DIR_CHOOSER_BIND_PREF, dirChooserBindPref);
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.pref_storage, rootKey);
    }

    private void dirChooseDialog(String path)
    {
        Intent i = new Intent(getActivity(), FileManagerDialog.class);
        FileManagerConfig config = new FileManagerConfig(path, null, null, FileManagerConfig.DIR_CHOOSER_MODE);
        i.putExtra(FileManagerDialog.TAG_CONFIG, config);

        startActivityForResult(i, DOWNLOAD_DIR_CHOOSE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == DOWNLOAD_DIR_CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.hasExtra(FileManagerDialog.TAG_RETURNED_PATH) && dirChooserBindPref != null) {
                String path = data.getStringExtra(FileManagerDialog.TAG_RETURNED_PATH);

                SharedPreferences pref = SettingsManager.getPreferences(getActivity());
                pref.edit().putString(dirChooserBindPref, path).apply();

                Preference p = findPreference(dirChooserBindPref);
                p.setSummary(path);
            }
        }
    }
}