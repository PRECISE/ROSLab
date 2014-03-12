#!/usr/bin/env python

# ROSLab authors: Peter Gebhard (pgeb@seas.upenn.edu), Junkil Park,  Nicola Bezzo (nicbezzo@seas.upenn.edu)

# Standard Python modules
import sys
import string
from collections import deque

#ROSPy modules
import rospy
from std_msgs.msg import Int32, Float32
from geometry_msgs.msg import TwistStamped, Twist
from nav_msgs.msg import Odometry
from sensor_msgs.msg import Joy

# QT modules
from PyQt4 import QtCore, QtGui

# PyQtGraph
import pyqtgraph as pg

try:
    _fromUtf8 = QtCore.QString.fromUtf8
except AttributeError:
    def _fromUtf8(s):
        return s

try:
    _encoding = QtGui.QApplication.UnicodeUTF8
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig, _encoding)
except AttributeError:
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig)

class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName(_fromUtf8("MainWindow"))
        MainWindow.resize(1274, 692)
        MainWindow.setMinimumSize(QtCore.QSize(0, 0))
        MainWindow.setMaximumSize(QtCore.QSize(1275, 693))
        MainWindow.setUnifiedTitleAndToolBarOnMac(True)
        self.centralWidget = QtGui.QWidget(MainWindow)
        self.centralWidget.setObjectName(_fromUtf8("centralWidget"))
        self.startButton = QtGui.QPushButton(self.centralWidget)
        self.startButton.setGeometry(QtCore.QRect(10, 220, 181, 81))
        font = QtGui.QFont()
        font.setPointSize(18)
        font.setBold(True)
        font.setWeight(75)
        self.startButton.setFont(font)
        self.startButton.setStyleSheet(_fromUtf8("QPushButton:checked {background: rgba(60, 179, 113, 255);}"))
        self.startButton.setCheckable(True)
        self.startButton.setObjectName(_fromUtf8("startButton"))
        self.tabWidget = QtGui.QTabWidget(self.centralWidget)
        self.tabWidget.setEnabled(False)
        self.tabWidget.setGeometry(QtCore.QRect(200, 10, 1061, 651))
        self.tabWidget.setTabPosition(QtGui.QTabWidget.North)
        self.tabWidget.setTabShape(QtGui.QTabWidget.Rounded)
        self.tabWidget.setObjectName(_fromUtf8("tabWidget"))
        self.control_tab = QtGui.QWidget()
        self.control_tab.setObjectName(_fromUtf8("control_tab"))
        self.outputPlotLabel = QtGui.QLabel(self.control_tab)
        self.outputPlotLabel.setEnabled(False)
        self.outputPlotLabel.setGeometry(QtCore.QRect(470, 10, 51, 16))
        self.outputPlotLabel.setObjectName(_fromUtf8("outputPlotLabel"))
        self.inputPlotLabel = QtGui.QLabel(self.control_tab)
        self.inputPlotLabel.setEnabled(False)
        self.inputPlotLabel.setGeometry(QtCore.QRect(10, 10, 41, 16))
        self.inputPlotLabel.setObjectName(_fromUtf8("inputPlotLabel"))
        self.inputPlot = pg.PlotWidget(self.control_tab)
        self.inputPlot.setGeometry(QtCore.QRect(10, 30, 441, 581))
        self.inputPlot.setVerticalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.inputPlot.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.inputPlot.setObjectName(_fromUtf8("inputPlot"))
        self.outputPlot = pg.PlotWidget(self.control_tab)
        self.outputPlot.setGeometry(QtCore.QRect(470, 30, 571, 581))
        self.outputPlot.setVerticalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.outputPlot.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)
        self.outputPlot.setObjectName(_fromUtf8("outputPlot"))
        self.tabWidget.addTab(self.control_tab, _fromUtf8(""))
        self.controlButton = QtGui.QPushButton(self.centralWidget)
        self.controlButton.setGeometry(QtCore.QRect(10, 310, 181, 81))
        font = QtGui.QFont()
        font.setPointSize(18)
        font.setBold(True)
        font.setWeight(75)
        self.controlButton.setFont(font)
        self.controlButton.setStyleSheet(_fromUtf8("QPushButton:checked {background: rgba(60, 179, 113, 255);}"))
        self.controlButton.setCheckable(True)
        self.controlButton.setObjectName(_fromUtf8("controlButton"))
        MainWindow.setCentralWidget(self.centralWidget)
        self.menuBar = QtGui.QMenuBar(MainWindow)
        self.menuBar.setGeometry(QtCore.QRect(0, 0, 1274, 22))
        self.menuBar.setNativeMenuBar(False)
        self.menuBar.setObjectName(_fromUtf8("menuBar"))
        self.menuFile = QtGui.QMenu(self.menuBar)
        self.menuFile.setObjectName(_fromUtf8("menuFile"))
        MainWindow.setMenuBar(self.menuBar)
        self.actionQuit = QtGui.QAction(MainWindow)
        self.actionQuit.setMenuRole(QtGui.QAction.QuitRole)
        self.actionQuit.setObjectName(_fromUtf8("actionQuit"))
        self.actionAbout = QtGui.QAction(MainWindow)
        self.actionAbout.setMenuRole(QtGui.QAction.AboutRole)
        self.actionAbout.setObjectName(_fromUtf8("actionAbout"))
        self.menuFile.addAction(self.actionAbout)
        self.menuFile.addAction(self.actionQuit)
        self.menuBar.addAction(self.menuFile.menuAction())

        self.retranslateUi(MainWindow)
        self.tabWidget.setCurrentIndex(0)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)
        MainWindow.setTabOrder(self.startButton, self.tabWidget)

    def retranslateUi(self, MainWindow):
        MainWindow.setWindowTitle(_translate("MainWindow", "ROSLab", None))
        self.startButton.setText(_translate("MainWindow", "Start", None))
        self.outputPlotLabel.setText(_translate("MainWindow", "Output", None))
        self.inputPlotLabel.setText(_translate("MainWindow", "Input", None))
        self.tabWidget.setTabText(self.tabWidget.indexOf(self.control_tab), _translate("MainWindow", "Control", None))
        self.controlButton.setText(_translate("MainWindow", $control_button_name$, None))
        self.menuFile.setTitle(_translate("MainWindow", "File", None))
        self.actionQuit.setText(_translate("MainWindow", "Quit", None))
        self.actionAbout.setText(_translate("MainWindow", "About", None))


class ROSLabWindow(QtGui.QMainWindow):
    def __init__(self):
        super(ROSLabWindow, self).__init__()

    def init_window(self):
        self.ui.setupUi(self)
        self.init_data_structs()
        self.init_widgets()

    def init_data_structs(self):
        self.trimIncrement = 0.001
        self.windowSize = 300
        self.datastructs = []

        #self.$port_name$_subscriber_data = deque(maxlen=self.windowSize)
        #self.datastructs.append(self.$port_name$_subscriber_data)
        $subscriber_data_defs$

    def init_widgets(self):
        self.widgets = [
            self.ui.tabWidget,
            self.ui.outputPlot,
            self.ui.outputPlotLabel,
            self.ui.inputPlot,
            self.ui.inputPlotLabel,
            self.ui.controlButton,
            #self.ui.controlButton,
            #$button_list$
        ]
        self.init_signals()
        self.init_plots()

        self.enableAllElements()

    def init_signals(self):
        self.ui.actionAbout.triggered.connect(self.about)
        self.ui.actionQuit.triggered.connect(self.fileQuit)
        self.ui.startButton.toggled.connect(self.start)
        self.ui.controlButton.toggled.connect(self.control)
        #self.ui.controlButton.toggled.connect(self.control)
        #$button_signals$

    def init_plots(self):
        self.ui.inputPlot.disableAutoRange(pg.ViewBox.XAxis)
        self.ui.inputPlot.disableAutoRange(pg.ViewBox.YAxis)
        self.ui.inputPlot.setXRange(-1.1, 1.1, 0)
        self.ui.inputPlot.setYRange(-1.1, 1.1, 0)
        self.ui.inputPlot.setBackground('w')
        self.ui.inputPlot.hideButtons()
        self.ui.inputPlot.showGrid(True, True)

        #self.roslabSub1Plot1 = self.ui.inputPlot.plot(self.roslabSub1Data1, self.roslabSub1Data2, symbol='o', symbolPen=pg.mkPen(color='r'), symbolBrush=pg.mkBrush(color='r'), symbolSize=20)
        #self.roslabSub1Plot = self.ui.inputPlot.plot(self.roslabSub1Data)
        #self.$port_name$_subscriber_plot = self.ui.inputPlot.plot(self.$port_name$_subscriber_data)
        $subscriber_input_data_plots$

        self.ui.outputPlot.setBackground('w')
        self.ui.outputPlot.hideButtons()
        self.ui.outputPlot.showGrid(False, True)
        labelStyle = {'font-size': '16pt'}
        self.ui.outputPlot.setLabel('bottom', 'time', **labelStyle)

        #self.roslabSub1Plot = self.ui.outputPlot.plot(self.roslabSub1Data)
        #self.$port_name$_subscriber_plot = self.ui.outputPlot.plot(self.$port_name$_subscriber_data)
        $subscriber_output_data_plots$

        self.plotTimer = QtCore.QTimer()
        self.plotTimer.timeout.connect(self.updatePlots)

    def startPlotTimers(self):
        timerMsec = 500
        self.plotTimer.start(timerMsec)

    def stopPlotTimers(self):
        self.plotTimer.stop()

    def about(self):
        about = QtGui.QDialog()
        #about.ui = Ui_AboutDialog()
        #about.ui.setupUi(about)
        #about.exec_()

    def fileQuit(self):
        if self.ui.startButton.isChecked():
            self.stop_ros_comm()
        self.close()

    def closeEvent(self, ce):
        self.fileQuit()

    def zeroData(self):
        for data in self.datastructs:
            data.clear()

    def enableAllElements(self):
        for widget in self.widgets:
            widget.setEnabled(True)
        self.zeroData()

    def disableAllElements(self):
        self.control(False)

        for widget in self.widgets:
            widget.setEnabled(False)

    def start(self, checked):
        if checked:
            res = self.remote.startLandshark()
            self.start_ros_comm()
            self.enableAllElements()
            self.startPlotTimers()
            self.zeroData()
        else:
            self.stopPlotTimers()
            self.disableAllElements()
            self.stop_ros_comm()
            res = self.remote.stopLandshark()
        self.ui.startButton.setChecked(res)

    def control(self, checked):
        if checked:
            res = self.remote.startControl()
            self.zeroData()
        else:
            res = self.remote.stopControl()
        self.ui.controlButton.setChecked(res)

    def updatePlots(self):
        """ Redraws the plots
        """
        #self.roslabSub1Plot1.setData(self.roslabSub1Data1, self.roslabSub1Data2)
        #self.$port_name$_subscriber_plot.setData(self.$port_name$_subscriber_data)
        $subscriber_data_plot_updates$

    def start_ros_comm(self):
        # Initialize ROSLab GUI node
        rospy.init_node('roslab_gui', disable_signals=True)

        self.pubsubs = []

        # Subscribe to ROSLab topics
        #self.roslabSub1 = rospy.Subscriber("/roslab_gui/roslabSub1", Float32, self.roslabSub1)
        #self.$port_name$_subscriber = rospy.Subscriber("/roslab_gui/$port_topic$", $port_type$, self.$port_name$_subscriber_callback)
        #self.pubsubs.append(self.$port_name$_subscriber)
        $subscribers$

        # Publish to ROSLab topics
        #self.roslabPub1 = rospy.Publisher("/roslab_gui/roslabPub1", Float32)
        #self.$port_name$_publisher = rospy.Publisher("/roslab_gui/$port_topic$", $port_type$)
        #self.pubsubs.append(self.$port_name$_publisher)
        #$publishers$

        return True

    def stop_ros_comm(self):
        # Unregister ROSLab topics
        for topic in self.pubsubs:
            topic.unregister()

        #rospy.signal_shutdown("Turning off ROSPy") TODO - How do we restart ROSPy
        #  Keep in mind ROSPy doesn't properly start back up when this is uncommented.

        return True

    #def roslabSub1(self, msg):
        #self.roslabSub1Data1[0] = -msg.axes[3]
        #self.roslabSub1Data.append(msg.linear.x)
        #self.roslabSub1Data2[0] = msg.axes[4]

    #def roslabSub2(self, msg):
        #self.roslabSub2Data1.append(msg.angular.z)
        #self.roslabSub2Data2.append(msg.linear.x)

    #def $port_name$_subscriber_callback(self, msg):
        #self.$port_name$_subscriber_data.append($subscriber_field$)
    $subscriber_callbacks$


class RLWindow(ROSLabWindow):
    def __init__(self):
        super(RLWindow, self).__init__()
        self.ui = Ui_MainWindow()
        self.init_window()


def main():
    app = QtGui.QApplication(sys.argv)
    h = RLWindow()
    h.show()
    app.aboutToQuit.connect(h.fileQuit)
    app.lastWindowClosed.connect(app.quit)
    sys.exit(app.exec_())

if __name__ == "__main__":
    main()
