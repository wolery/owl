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

//****************************************************************************

package object gui
{
  type Chord       = Seq[core.Pitch]
  type Pitches     = Seq[core.Pitch]
  type Tick        = Long
  type Tempo       = ℝ // in BPM
  type BPM         = ℝ
  type Millisecond = ℕ

  type fx          = javafx.fxml.FXML
  type Pane        = javafx.scene.layout.Pane

  type Logging     = com.wolery.util.Logging
  val  manifest    = com.wolery.util.manifest
  type Application = com.wolery.fx.util.Application
}

//****************************************************************************
