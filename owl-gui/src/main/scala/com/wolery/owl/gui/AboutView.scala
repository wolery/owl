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

//****************************************************************************

import javafx.scene.Scene
import javafx.scene.control.{Label,ProgressBar}
import javafx.stage.{Modality,Stage,StageStyle}

//****************************************************************************

class AboutController(stage: Stage) extends Logging
{
  @fx var m_root: Pane        = _
  @fx var m_vers: Label       = _
  @fx var m_bld:  Label       = _
  @fx var m_task: Label       = _
  @fx var m_copy: Label       = _
  @fx var m_prog: ProgressBar = _

  def initialize(): Unit =
  {
    log.debug("initialize()")

    m_vers.setText  (AboutView.version)
    m_bld. setText  (AboutView.build)
    m_task.setVisible(false)
    m_prog.setVisible(false)

    stage.setAlwaysOnTop(true)
    stage.setResizable(false)
  }
}

object AboutView
{
  def apply(owner: Stage): Unit =
  {
    val stage = new Stage
    val (v,_) = util.load.view("SplashView",new AboutController(stage))

    stage.setScene    (new Scene(v))
    stage.initOwner   (owner);
    stage.initStyle   (StageStyle.DECORATED)
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.showAndWait ();
  }

  def build:String =
  {
    val f = new java.text.SimpleDateFormat("yy-MM-dd'T'HH:mm:ss")

    manifest.attributes
            .get("Built")
            .map(f.parse(_).toString)
            .getOrElse("BUJILD")
  }

  def version =
  {
    val v =     manifest.attributes
                .get("Specification-Version")
                .map("Version " + _)
                .getOrElse("VERSION")
    val p =    manifest.attributes
                .get("Profile")
                .getOrElse("")
            v + " " + p
  }
}

//****************************************************************************
