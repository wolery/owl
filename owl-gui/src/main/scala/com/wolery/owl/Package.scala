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
//*
//*
//****************************************************************************

package com.wolery

//****************************************************************************

package object owl
{
  import core._

  type ℕ         = core.ℕ
  type ℤ         = core.ℤ
  type ℝ         = core.ℝ
  type Bool      = core.Bool
  type Name      = core.Name
  type Chord     = Seq[Pitch]
  type Pitches   = Seq[Pitch]

  type Pane      = javafx.scene.layout.Pane
  type Tick      = Long
  type Tempo     = ℝ // in BPM
  type BPM       = ℝ
  type Millisecond = ℕ
}

//****************************************************************************
