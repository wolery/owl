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

import javax.sound.midi.{MidiSystem, Sequence, Sequencer, Synthesizer}

import javafx.concurrent.Task
import javafx.stage.Stage

import com.wolery.owl.gui.{Application, MainView, SplashView}
import com.wolery.owl.gui.util.implicits.asTask
import com.wolery.owl.midi.Transport

//****************************************************************************

object owl extends Application
{
  val sequencer:   Sequencer   = MidiSystem.getSequencer()
  val synthesizer: Synthesizer = MidiSystem.getSynthesizer()

  override
  def init(): Unit =
  {
    stylesheet = Some("/css/Owl.css")
  }

  def task: Task[Unit] =
  {
    synthesizer.open()
    sequencer.open()
    sequencer.getTransmitter.setReceiver(synthesizer.getReceiver)
  }

  def start(stage: Stage): Unit =
  {
    log.debug("start(stage)")

    SplashView(stage,task,MainView())
  }

  override
  def stop() =
  {
    sequencer.close()
    synthesizer.close()
  }

  def open(sequence: Sequence): Transport =
  {
    sequencer.setSequence(sequence)

    new Transport(sequencer)
  }
}

//****************************************************************************
