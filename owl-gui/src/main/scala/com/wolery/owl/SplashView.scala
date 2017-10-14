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

import com.wolery.owl.utils.implicits._
import com.wolery.owl.utils.load

import javafx.animation.FadeTransition
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.ActionEvent
import javafx.fxml.{ FXML ⇒ fx }
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration._

//****************************************************************************

class SplashController(stage: Stage,task: Task[_],continue: () ⇒ Unit)
{
  @fx var root: Pane        = _
  @fx var bar:  ProgressBar = _
  @fx var txt:  Label       = _

  task.setOnSucceeded((_: WorkerStateEvent) ⇒ onSucceeded())

  def initialize() =
  {
    txt.textProperty.bind    (task.messageProperty)
    bar.progressProperty.bind(task.progressProperty)

    stage.setScene(new Scene(root,Color.TRANSPARENT))
    stage.initStyle(StageStyle.TRANSPARENT)
    stage.setAlwaysOnTop(true)
    stage.show()

    new Thread(task).start()
  }

  def onSucceeded() =
  {
    val t = new FadeTransition(seconds(1.0),root)

    t.setFromValue (1.0)
    t.setToValue   (0.0)
    t.setOnFinished((_: ActionEvent) ⇒ stage.hide())
    t.play()

    continue()
  }
}

//****************************************************************************

object splash
{
  def apply(stage: Stage,task: Task[Unit],done: () ⇒ Unit): Unit =
  {
    load.view("SplashView",new SplashController(stage,task,done))
  }
}

//****************************************************************************
