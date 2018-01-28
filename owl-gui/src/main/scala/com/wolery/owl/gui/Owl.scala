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

import com.wolery.owl.gui.util.implicits.asTask
import javafx.concurrent.Task
import javafx.stage.Stage
import javax.sound.midi.{ MidiSystem, Sequencer, Synthesizer }

//****************************************************************************

object owl extends util.Application
{
  val sequencer:   Sequencer   = MidiSystem.getSequencer()
  val synthesizer: Synthesizer = MidiSystem.getSynthesizer()

  val initialize: Task[Unit] =
  {
  //load.font("fontawesome-webfont")
    synthesizer.open()
  //synthesizer.loadAllInstruments(load.soundbank("FluidR3 GM2-2"))
    sequencer.open()
    sequencer.getTransmitter.setReceiver(synthesizer.getReceiver)
  }

  def start(stage: Stage): Unit =
  {
    ConsoleView(stage)
//    splash(stage,initialize,() ⇒ MainView())
  }

  override
  def stop() =
  {
    sequencer.close()
    synthesizer.close()
  }
}

//****************************************************************************
