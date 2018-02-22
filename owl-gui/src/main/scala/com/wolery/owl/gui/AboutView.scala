//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package gui

import javafx.scene.Scene
import javafx.scene.control.{Label,ProgressBar}
import javafx.stage.{Modality,Stage}

/**
 * @author Jonathon Bell
 */
class AboutController(stage: Stage) extends Logging
{
  @fx var m_root: Pane        = _                        //
  @fx var m_vers: Label       = _                        //
  @fx var m_time: Label       = _                        //
  @fx var m_copy: Label       = _                        //
  @fx var m_task: Label       = _                        //
  @fx var m_prog: ProgressBar = _                        //

  def initialize(): Unit =
  {
    log.debug("initialize()")                            //

    m_vers.setText(manifest.format(m_vers.getText))      //
    m_time.setText(manifest.format(m_time.getText))      //

    stage.setAlwaysOnTop(true)                           //
    stage.setResizable(false)                            //
  }
}

/**
 * @author Jonathon Bell
 */
object AboutView
{
  def apply(owner: Stage): Unit =
  {
    val s = new Stage                                    //
    val c = new AboutController(s)                       //
    val (v,_) = util.load.view("SplashView",c)           //

    s.initOwner   (owner);                               //
    s.setScene    (new Scene(v))                         //
    s.setTitle    ("About Owl")                          //
    s.initModality(Modality.APPLICATION_MODAL);          //
    s.showAndWait ();                                    //
  }
}

//****************************************************************************
