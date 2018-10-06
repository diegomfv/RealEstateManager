package com.diegomfv.android.realestatemanager.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.diegomfv.android.realestatemanager.data.entities.Agent;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 06/10/2018.
 */
@Dao
public interface AgentDao {

    // -------------------
    // READ

    @Query("SELECT * FROM agent ORDER BY email")
    List<Agent> getAllAgents();

    // -------------------
    // INSERT
    @Insert
    long insertAgent(Agent agent);

    // -------------------
    // UPDATE
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateAgent(Agent agent);

    // -------------------
    // DELETE


}
