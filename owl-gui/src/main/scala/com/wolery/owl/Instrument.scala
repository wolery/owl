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

package com.wolery.owl

//****************************************************************************

import core._

//****************************************************************************

trait Instrument
{
  def lowest              : Pitch
  def highest             : Pitch
  def playable            : Seq[Pitch] = lowest to highest

  def view(fxml: String)  : (Pane,Controller)
}

//****************************************************************************
