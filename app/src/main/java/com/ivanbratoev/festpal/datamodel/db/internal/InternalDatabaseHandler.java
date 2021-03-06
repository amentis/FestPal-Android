/*
 * Copyright 2015 Ivan Bratoev
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ivanbratoev.festpal.datamodel.db.internal;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.ivanbratoev.festpal.datamodel.Concert;
import com.ivanbratoev.festpal.datamodel.Festival;

import java.util.Date;

/**
 * Helper class for accessing the internal database
 */
public class InternalDatabaseHandler {

    private InternalDBHelper dbHelper;

    /**
     *
     * @param context application contexts
     */
    public InternalDatabaseHandler(Context context) {
        dbHelper = new InternalDBHelper(context);
    }

    /**
     *
     * @return true if festivals are recorded in the database, false otherwise
     */
    public boolean hasFestivals(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                new String[]{InternalDBContract.FestivalEntry._ID},
                null, null, null, null, null);

        boolean result = (cursor.getCount() != 0);

        cursor.close();

        return result;
    }

    /**
     *
     * @param festival festival to check for
     * @return true if there are concerts recorded for the input festival, false otherwise
     */
    public boolean festivalHasConcerts(Festival festival) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                new String[]{InternalDBContract.FestivalEntry._ID},
                InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL + "=?",
                new String[]{String.valueOf(festival.getId())},
                null, null, null);

        boolean result = (cursor.getCount() != 0);

        cursor.close();

        return result;
    }

    /**
     * @return an array of all festivals in the internal db
     */
    public Festival[] getFestivals(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                null, null, null, null, null, null);

        Festival[] result = new Festival[cursor.getCount()];

        cursor.moveToFirst();
        int i = 0;

        while (!cursor.isAfterLast()){
            result[i] = new Festival(
                    cursor.getLong(cursor.getColumnIndex(InternalDBContract.FestivalEntry._ID)),
                    cursor.getInt(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_EXTERNAL_ID)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES)),
                    cursor.getString(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_OWNER)),
                    (1 == cursor.getInt(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL))),
                    cursor.getInt(
                            cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_VOTES))
            );
            i++;
            cursor.moveToNext();
        }

        cursor.close();

        return result;
    }

    /**
     *
     * @param id of the festival
     * @return Festival object found
     */
    public Festival getFestival(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                null, "?=?",
                new String[]{InternalDBContract.FestivalEntry._ID, String.valueOf(id)},
                null, null, null);

        cursor.moveToFirst();
        Festival festival = new Festival(
                cursor.getLong(cursor.getColumnIndex(InternalDBContract.FestivalEntry._ID)),
                cursor.getLong(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_EXTERNAL_ID)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES)),
                cursor.getString(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_OWNER)),
                (1 == cursor.getInt(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL))),
                cursor.getInt(
                        cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_VOTES))
        );
        cursor.close();
        return festival;
    }

    /**
     * Add a festival to the internal DB
     * @param festival the festival information to insert
     * @return internal ID of the added object or -1 on fail
     */
    public long addFestival(Festival festival){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_EXTERNAL_ID, festival.getExternalId());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME, festival.getName());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION, festival.getDescription());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY, festival.getCountry());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY, festival.getCity());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS, festival.getAddress());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE, festival.getGenre());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES, festival.getPrices());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_OWNER, festival.getOwner());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL, festival.isOfficial());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_VOTES, festival.getVotes());

        return db.insert(InternalDBContract.FestivalEntry.TABLE_NAME, null, values);
    }

    /**
     * alter the values of the record of a festival in the internal database
     * @param id id used to find the record
     * @param name new name to set or <code>null</code> to keep the field unmodified
     * @param description new description to set or <code>null</code> to keep the field unmodified
     * @param country new country to set or <code>null</code> to keep the field unmodified
     * @param city new city to set or <code>null</code> to keep the field unmodified
     * @param address new address to set or <code>null</code> to keep the field unmodified
     * @param genre new genre to set or <code>null</code> to keep the field unmodified
     * @param prices new prices to set or <code>null</code> to keep the field unmodified
     * @param owner new uploader to set or <code>null</code> to keep the field unmodified
     * @param official new official to set or <code>null</code> to keep the field unmodified
     * @param votes new votes count to set or <code>null</code> to keep the field unmodified
     */
    public void editFestival(long id, @Nullable String name, @Nullable String description,
                             @Nullable String country, @Nullable String city,
                             @Nullable String address, @Nullable String genre,
                             @Nullable String prices, @Nullable String owner,
                             @Nullable Boolean official, @Nullable Integer votes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (name != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME, name);
        if (description != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION, description);
        if (country != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY, country);
        if (city != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY, city);
        if (address != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS, address);
        if (genre != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE, genre);
        if (prices != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES, prices);
        if (owner != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_OWNER, owner);
        if (official != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL, official);
        if (votes != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_VOTES, votes);

        db.update(InternalDBContract.FestivalEntry.TABLE_NAME,
                values,
                InternalDBContract.FestivalEntry._ID + " LIKE ? ",
                new String[]{String.valueOf(id)});
    }

    /**
     * remove festival
     * @param id id of the festival to remove
     */
    public void removeFestival(long id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(InternalDBContract.FestivalEntry.TABLE_NAME,
                InternalDBContract.FestivalEntry._ID + "LIKE ?",
                new String[]{String.valueOf(id)});
    }

    /**
     *
     * @param festival festival in which the concerts are to be listed
     * @return all concerts in the festival
     */
    public Concert[] getConcerts(Festival festival){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.ConcertEntry.TABLE_NAME,
                null,
                "WHERE " + InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL + "=?",
                new String[]{String.valueOf(festival.getId())},
                null, null, null);

        Concert[] result = new Concert[cursor.getCount()];

        cursor.moveToFirst();
        int i = 0;

        while (!cursor.isAfterLast()){
            result[i] = new Concert(
                    cursor.getLong(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry._ID)),
                    cursor.getLong(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry.COLUMN_NAME_EXTERNAL_ID)),
                    festival,
                    cursor.getString(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST)),
                    cursor.getInt(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry.COLUMN_NAME_STAGE)),
                    cursor.getInt(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry.COLUMN_NAME_DAY)),
                    new Date(cursor.getInt(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry.COLUMN_NAME_START))),
                    new Date(cursor.getInt(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry.COLUMN_NAME_END))),
                    (1 == cursor.getInt(cursor.getColumnIndex(
                            InternalDBContract.ConcertEntry.COLUMN_NAME_NOTIFY)))
            );
            i++;
            cursor.moveToNext();
        }

        cursor.close();

        return result;
    }

    /**
     *
     * @param festival festival hosting the concert
     * @param id id of the concert
     * @return the concert object found
     */
    public Concert getConcert(Festival festival, long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                null, "?=? AND ?=?",
                new String[]{InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL,
                        String.valueOf(festival.getId()),
                        InternalDBContract.ConcertEntry._ID,
                        String.valueOf(id)},
                null, null, null);

        cursor.moveToFirst();
        Concert concert = new Concert(
                cursor.getLong(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry._ID)),
                cursor.getLong(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry.COLUMN_NAME_EXTERNAL_ID)),
                festival,
                cursor.getString(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST)),
                cursor.getInt(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry.COLUMN_NAME_STAGE)),
                cursor.getInt(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry.COLUMN_NAME_DAY)),
                new Date(cursor.getInt(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry.COLUMN_NAME_START))),
                new Date(cursor.getInt(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry.COLUMN_NAME_END))),
                (1 == cursor.getInt(cursor.getColumnIndex(
                        InternalDBContract.ConcertEntry.COLUMN_NAME_NOTIFY)))
        );
        cursor.close();
        return concert;
    }

    /**
     * add concert to the internal DB
     * @param concert concert info to insert
     * @return result from DB insert
     */
    public long addConcert(Concert concert){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL,
                concert.getFestival().getId());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST,
                concert.getArtist());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_STAGE,
                concert.getStage());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_DAY,
                concert.getDay());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_START,
                concert.getStart().getTime());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_END,
                concert.getEnd().getTime());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_NOTIFY,
                concert.isToNotify());

        return db.insert(InternalDBContract.ConcertEntry.TABLE_NAME, null, values);
    }

    /**
     * Alter the information of a concert entry
     * @param concertId id of the concert
     * @param externalID external id of the concert
     * @param festival new festival id to set or <code>null</code> to leave unmodified
     * @param artist new artist name to set or <code>null</code> to leave unmodified
     * @param stage new stage number to set or <code>null</code> to leave unmodified
     * @param day new day number to set or <code>null</code> to leave unmodified
     * @param start new starting datetime to set or <code>null</code> to leave unmodified
     * @param end new ending datetime to set or <code>null</code> to leave unmodified
     * @param notify new notify value to set or <code>null</code> to leave unmodified
     */
    public void editConcert(long concertId, Long externalID, Long festival,
                            String artist, Integer stage,
                            Integer day, Date start, Date end, Boolean notify) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (externalID != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_EXTERNAL_ID, externalID);
        if (festival != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL, festival);
        if (artist != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST, artist);
        if (stage != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_STAGE, stage);
        if (day != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_DAY, day);
        if (start != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_START, start.getTime());
        if (end != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_END, end.getTime());
        if (notify != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_NOTIFY, notify);

        db.update(InternalDBContract.ConcertEntry.TABLE_NAME,
                values,
                "?=?",
                new String[]{InternalDBContract.ConcertEntry._ID, String.valueOf(concertId)});

    }

    /**
     * remove concert from internal database
     * @param festival festival id of the festival the concert is part of
     * @param artist artist name
     */
    public void removeConcert(int festival, String artist){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(InternalDBContract.ConcertEntry.TABLE_NAME,
                InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL + " LIKE ? AND "
                        + InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST + " LIKE ? ",
                new String[]{String.valueOf(festival), artist});
    }
}
