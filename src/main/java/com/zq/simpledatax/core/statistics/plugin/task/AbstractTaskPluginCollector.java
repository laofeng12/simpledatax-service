package com.zq.simpledatax.core.statistics.plugin.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zq.simpledatax.common.constant.PluginType;
import com.zq.simpledatax.common.element.Record;
import com.zq.simpledatax.common.exception.DataXException;
import com.zq.simpledatax.common.plugin.TaskPluginCollector;
import com.zq.simpledatax.common.util.Configuration;
import com.zq.simpledatax.core.statistics.communication.Communication;
import com.zq.simpledatax.core.statistics.communication.CommunicationTool;
import com.zq.simpledatax.core.util.FrameworkErrorCode;

/**
 * Created by jingxing on 14-9-11.
 */
public abstract class AbstractTaskPluginCollector extends TaskPluginCollector {
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractTaskPluginCollector.class);

    private Communication communication;

    private Configuration configuration;

    private PluginType pluginType;

    public AbstractTaskPluginCollector(Configuration conf, Communication communication,
                                       PluginType type) {
        this.configuration = conf;
        this.communication = communication;
        this.pluginType = type;
    }

    public Communication getCommunication() {
        return communication;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public PluginType getPluginType() {
        return pluginType;
    }

    @Override
    final public void collectMessage(String key, String value) {
        this.communication.addMessage(key, value);
    }

    @Override
    public void collectDirtyRecord(Record dirtyRecord, Throwable t,
                                   String errorMessage) {

        if (null == dirtyRecord) {
            LOG.warn("脏数据record=null.");
            return;
        }

        if (this.pluginType.equals(PluginType.READER)) {
            this.communication.increaseCounter(
                    CommunicationTool.READ_FAILED_RECORDS, 1);
            this.communication.increaseCounter(
                    CommunicationTool.READ_FAILED_BYTES, dirtyRecord.getByteSize());
        } else if (this.pluginType.equals(PluginType.WRITER)) {
            this.communication.increaseCounter(
                    CommunicationTool.WRITE_FAILED_RECORDS, 1);
            this.communication.increaseCounter(
                    CommunicationTool.WRITE_FAILED_BYTES, dirtyRecord.getByteSize());
        } else {
            throw DataXException.asDataXException(
                    FrameworkErrorCode.RUNTIME_ERROR,
                    String.format("不知道的插件类型[%s].", this.pluginType));
        }
    }
}
