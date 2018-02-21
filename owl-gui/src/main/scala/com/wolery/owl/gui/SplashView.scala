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

import javafx.animation.FadeTransition
import javafx.concurrent.Task
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.{Stage,StageStyle}
import javafx.util.Duration.seconds

/**
 * @param stage     .
 * @param task      .
 * @param continue  .
 *
 * @author Jonathon Bell
 */
class SplashController
(
  stage:    Stage,
  task:     Task[_],
  continue: ⇒ Unit
)
extends AboutController(stage)
{
  /**
   *
   */
  override
  def initialize(): Unit =
  {
    log.debug("initialize()")
    super.initialize()

    m_task.textProperty.bind    (task.messageProperty)   //
    m_prog.progressProperty.bind(task.progressProperty)  //
    m_task.setVisible(true)                              //
    m_prog.setVisible(true)                              //

    task.setOnSucceeded(_ ⇒ onSucceeded())               //
    new Thread(task).start()                             //
  }

  /**
   *
   */
  def onSucceeded(): Unit =
  {
    log.debug("onSucceeded()")                           //

    val t = new FadeTransition(seconds(3.0),m_root)      //

    t.setFromValue (1.0)                                 //
    t.setToValue   (0.0)                                 //
    t.setOnFinished(_ ⇒ stage.close())                   //
    t.play()                                             //

    continue                                             //
  }
}

/**
 * @author Jonathon Bell
 */
object SplashView
{
  def apply(stage: Stage,task: Task[Unit],continue: ⇒ Unit): Unit =
  {
    val c     = new SplashController(stage,task,continue)//
    val (v,_) = util.load.view("SplashView",c)           //

    stage.setScene (new Scene(v,Color.TRANSPARENT))      //
    stage.initStyle(StageStyle.TRANSPARENT)              //
    stage.show()                                         //
  }
}

//****************************************************************************
