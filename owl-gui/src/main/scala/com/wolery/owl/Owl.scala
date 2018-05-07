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

import javafx.concurrent.Task
import javafx.stage.Stage

import com.wolery.owl.gui.{MainView, SplashView}
import com.wolery.owl.gui.util.load
import com.wolery.owl.gui.util.implicits.asTask
import com.wolery.owl.interpreter.{Interpreter,InterpreterConsole,ScalaInterpreter}
import com.wolery.owl.preferences.theme

import javax.sound.midi.{MidiSystem, Sequence, Sequencer, Synthesizer}

//****************************************************************************

object owl extends Application
{
  val interpreter: Interpreter = ScalaInterpreter
  val sequencer:   Sequencer   = MidiSystem.getSequencer()
  val synthesizer: Synthesizer = MidiSystem.getSynthesizer()

  override
  def init(): Unit =
  {
    stylesheet = load.theme(theme())
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

  def open(sequence: Sequence): com.wolery.owl.midi.Transport =
  {
    sequencer.setSequence(sequence)

    new com.wolery.owl.midi.Transport(sequencer)
  }

  def console(): Node =
  {
    new InterpreterConsole(interpreter)
  }
}

//****************************************************************************
