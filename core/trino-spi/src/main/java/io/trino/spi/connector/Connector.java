/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.spi.connector;

import io.trino.spi.eventlistener.EventListener;
import io.trino.spi.procedure.Procedure;
import io.trino.spi.session.PropertyMetadata;
import io.trino.spi.transaction.IsolationLevel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public interface Connector
{
    /**
     * Get handle resolver for this connector instance. If {@code Optional.empty()} is returned,
     * {@link ConnectorFactory#getHandleResolver()} is used instead.
     */
    default Optional<ConnectorHandleResolver> getHandleResolver()
    {
        return Optional.empty();
    }

    ConnectorTransactionHandle beginTransaction(IsolationLevel isolationLevel, boolean readOnly);

    /**
     * Guaranteed to be called at most once per transaction. The returned metadata will only be accessed
     * in a single threaded context.
     */
    ConnectorMetadata getMetadata(ConnectorTransactionHandle transactionHandle);

    /**
     * @throws UnsupportedOperationException if this connector does not support tables with splits
     */
    default ConnectorSplitManager getSplitManager()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support reading tables page at a time
     */
    default ConnectorPageSourceProvider getPageSourceProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support reading tables record at a time
     */
    default ConnectorRecordSetProvider getRecordSetProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support writing tables page at a time
     */
    default ConnectorPageSinkProvider getPageSinkProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support indexes
     */
    default ConnectorIndexProvider getIndexProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not support partitioned table layouts
     */
    default ConnectorNodePartitioningProvider getNodePartitioningProvider()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the set of system tables provided by this connector
     */
    default Set<SystemTable> getSystemTables()
    {
        return emptySet();
    }

    /**
     * @return the set of procedures provided by this connector
     */
    default Set<Procedure> getProcedures()
    {
        return emptySet();
    }

    /**
     * @return the system properties for this connector
     */
    default List<PropertyMetadata<?>> getSessionProperties()
    {
        return emptyList();
    }

    /**
     * @return the schema properties for this connector
     */
    default List<PropertyMetadata<?>> getSchemaProperties()
    {
        return emptyList();
    }

    /**
     * @return the analyze properties for this connector
     */
    default List<PropertyMetadata<?>> getAnalyzeProperties()
    {
        return emptyList();
    }

    /**
     * @return the table properties for this connector
     */
    default List<PropertyMetadata<?>> getTableProperties()
    {
        return emptyList();
    }

    /**
     * @return the materialized view properties for this connector
     */
    default List<PropertyMetadata<?>> getMaterializedViewProperties()
    {
        return emptyList();
    }

    /**
     * @return the column properties for this connector
     */
    default List<PropertyMetadata<?>> getColumnProperties()
    {
        return emptyList();
    }

    /**
     * @throws UnsupportedOperationException if this connector does not have an access control
     */
    default ConnectorAccessControl getAccessControl()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the event listeners provided by this connector
     */
    default Iterable<EventListener> getEventListeners()
    {
        return emptySet();
    }

    /**
     * Commit the transaction. Will be called at most once and will not be called if
     * {@link #rollback(ConnectorTransactionHandle)} is called.
     */
    default void commit(ConnectorTransactionHandle transactionHandle) {}

    /**
     * Rollback the transaction. Will be called at most once and will not be called if
     * {@link #commit(ConnectorTransactionHandle)} is called.
     * Note: calls to this method may race with calls to the ConnectorMetadata.
     */
    default void rollback(ConnectorTransactionHandle transactionHandle) {}

    /**
     * True if the connector only supports write statements in independent transactions.
     */
    default boolean isSingleStatementWritesOnly()
    {
        return true;
    }

    /**
     * Shutdown the connector by releasing any held resources such as
     * threads, sockets, etc. This method will only be called when no
     * queries are using the connector. After this method is called,
     * no methods will be called on the connector or any objects that
     * have been returned from the connector.
     */
    default void shutdown() {}

    default Set<ConnectorCapabilities> getCapabilities()
    {
        return emptySet();
    }
}
