/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.paimon.web.api.table;

import org.apache.paimon.types.DataType;

import javax.annotation.Nullable;

import java.util.Objects;

/**
 * This change represents the modification of the table including adding, modifying and dropping
 * column etc.
 */
public interface TableChange {
    static AddColumn add(ColumnMetadata column) {
        return new AddColumn(column, null);
    }

    static AddColumn add(ColumnMetadata column, @Nullable ColumnPosition position) {
        return new AddColumn(column, position);
    }

    static ModifyColumn modify(
            ColumnMetadata oldColumn,
            ColumnMetadata newColumn,
            @Nullable ColumnPosition columnPosition) {
        return new ModifyColumn(oldColumn, newColumn, columnPosition);
    }

    static ModifyColumnType modifyColumnType(ColumnMetadata oldColumn, DataType newType) {
        return new ModifyColumnType(oldColumn, newType);
    }

    static ModifyColumnName modifyColumnName(ColumnMetadata oldColumn, String newName) {
        return new ModifyColumnName(oldColumn, newName);
    }

    static ModifyColumnComment modifyColumnComment(ColumnMetadata oldColumn, String newComment) {
        return new ModifyColumnComment(oldColumn, newComment);
    }

    static ModifyColumnPosition modifyColumnPosition(
            ColumnMetadata oldColumn, ColumnPosition columnPosition) {
        return new ModifyColumnPosition(oldColumn, columnPosition);
    }

    static DropColumn dropColumn(String columnName) {
        return new DropColumn(columnName);
    }

    static SetOption set(String key, String value) {
        return new SetOption(key, value);
    }

    static RemoveOption remove(String key) {
        return new RemoveOption(key);
    }

    /** A table change to add a column. */
    class AddColumn implements TableChange {

        private final ColumnMetadata column;
        private final ColumnPosition position;

        private AddColumn(ColumnMetadata column, ColumnPosition position) {
            this.column = column;
            this.position = position;
        }

        public ColumnMetadata getColumn() {
            return column;
        }

        @Nullable
        public ColumnPosition getPosition() {
            return position;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AddColumn)) {
                return false;
            }
            AddColumn addColumn = (AddColumn) o;
            return Objects.equals(column, addColumn.column)
                    && Objects.equals(position, addColumn.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, position);
        }

        @Override
        public String toString() {
            return "AddColumn{" + "column=" + column + ", position=" + position + '}';
        }
    }

    /** A base schema change to modify a column. */
    class ModifyColumn implements TableChange {

        protected final ColumnMetadata oldColumn;
        protected final ColumnMetadata newColumn;

        protected final @Nullable ColumnPosition newPosition;

        public ModifyColumn(
                ColumnMetadata oldColumn,
                ColumnMetadata newColumn,
                @Nullable ColumnPosition newPosition) {
            this.oldColumn = oldColumn;
            this.newColumn = newColumn;
            this.newPosition = newPosition;
        }

        public ColumnMetadata getOldColumn() {
            return oldColumn;
        }

        public ColumnMetadata getNewColumn() {
            return newColumn;
        }

        public @Nullable ColumnPosition getNewPosition() {
            return newPosition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ModifyColumn)) {
                return false;
            }
            ModifyColumn that = (ModifyColumn) o;
            return Objects.equals(oldColumn, that.oldColumn)
                    && Objects.equals(newColumn, that.newColumn)
                    && Objects.equals(newPosition, that.newPosition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(oldColumn, newColumn, newPosition);
        }

        @Override
        public String toString() {
            return "ModifyColumn{"
                    + "oldColumn="
                    + oldColumn
                    + ", newColumn="
                    + newColumn
                    + ", newPosition="
                    + newPosition
                    + '}';
        }
    }

    /** A table change that modify the column data type. */
    class ModifyColumnType extends ModifyColumn {

        private ModifyColumnType(ColumnMetadata oldColumn, DataType newType) {
            super(oldColumn, oldColumn.copy(newType), null);
        }

        /** Get the column type for the new column. */
        public DataType getNewType() {
            return newColumn.type();
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ModifyColumnType) && super.equals(o);
        }

        @Override
        public String toString() {
            return "ModifyColumnType{" + "Column=" + oldColumn + ", newType=" + getNewType() + '}';
        }
    }

    /** A table change to modify the column name. */
    class ModifyColumnName extends ModifyColumn {

        private ModifyColumnName(ColumnMetadata oldColumn, String newName) {
            super(oldColumn, createNewColumn(oldColumn, newName), null);
        }

        private static ColumnMetadata createNewColumn(ColumnMetadata oldColumn, String newName) {
            return new ColumnMetadata(newName, oldColumn.type(), oldColumn.description());
        }

        public String getOldColumnName() {
            return oldColumn.name();
        }

        /** Returns the new column name after renaming the column name. */
        public String getNewColumnName() {
            return newColumn.name();
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ModifyColumnName) && super.equals(o);
        }

        @Override
        public String toString() {
            return "ModifyColumnName{"
                    + "Column="
                    + oldColumn
                    + ", newName="
                    + getNewColumnName()
                    + '}';
        }
    }

    /** A table change to modify the column comment. */
    class ModifyColumnComment extends ModifyColumn {

        private final String newComment;

        private ModifyColumnComment(ColumnMetadata oldColumn, String newComment) {
            super(oldColumn, oldColumn.setComment(newComment), null);
            this.newComment = newComment;
        }

        /** Get the new comment for the column. */
        public String getNewComment() {
            return newComment;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ModifyColumnComment) && super.equals(o);
        }

        @Override
        public String toString() {
            return "ModifyColumnComment{"
                    + "Column="
                    + oldColumn
                    + ", newComment="
                    + newComment
                    + '}';
        }
    }

    /** A table change to modify the column position. */
    class ModifyColumnPosition extends ModifyColumn {

        public ModifyColumnPosition(ColumnMetadata oldColumn, ColumnPosition newPosition) {
            super(oldColumn, oldColumn, newPosition);
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ModifyColumnPosition) && super.equals(o);
        }

        @Override
        public String toString() {
            return "ModifyColumnPosition{"
                    + "Column="
                    + oldColumn
                    + ", newPosition="
                    + newPosition
                    + '}';
        }
    }

    /** A table change to drop the column. */
    class DropColumn implements TableChange {

        private final String columnName;

        private DropColumn(String columnName) {
            this.columnName = columnName;
        }

        /** Returns the column name. */
        public String getColumnName() {
            return columnName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DropColumn)) {
                return false;
            }
            DropColumn that = (DropColumn) o;
            return Objects.equals(columnName, that.columnName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(columnName);
        }

        @Override
        public String toString() {
            return "DropColumn{" + "columnName=" + columnName + '}';
        }
    }

    /** A table change to set the table option. */
    class SetOption implements TableChange {

        private final String key;
        private final String value;

        private SetOption(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /** Returns the Option key to set. */
        public String getKey() {
            return key;
        }

        /** Returns the Option value to set. */
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SetOption)) {
                return false;
            }
            SetOption setOption = (SetOption) o;
            return Objects.equals(key, setOption.key) && Objects.equals(value, setOption.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "SetOption{" + "key=" + key + ", value=" + value + '}';
        }
    }

    /** A table change to remove the table option. */
    class RemoveOption implements TableChange {

        private final String key;

        public RemoveOption(String key) {
            this.key = key;
        }

        /** Returns the Option key to reset. */
        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RemoveOption)) {
                return false;
            }
            RemoveOption that = (RemoveOption) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override
        public String toString() {
            return "RemoveOption{" + "key=" + key + '}';
        }
    }

    /** The position of the modified or added column. */
    interface ColumnPosition {

        /** Get the position to place the column at the first. */
        static ColumnPosition first() {
            return First.INSTANCE;
        }

        /** Get the position to place the column after the specified column. */
        static ColumnPosition after(String column) {
            return new After(column);
        }
    }

    /** Column position FIRST means the specified column should be the first column. */
    final class First implements ColumnPosition {
        private static final First INSTANCE = new First();

        private First() {}

        @Override
        public String toString() {
            return "FIRST";
        }
    }

    /** Column position AFTER means the specified column should be put after the given `column`. */
    final class After implements ColumnPosition {
        private final String column;

        private After(String column) {
            this.column = column;
        }

        public String column() {
            return column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof After)) {
                return false;
            }
            After after = (After) o;
            return Objects.equals(column, after.column);
        }

        @Override
        public int hashCode() {
            return Objects.hash(column);
        }

        @Override
        public String toString() {
            return String.format("AFTER %s", escapeIdentifier(column));
        }

        private static String escapeBackticks(String s) {
            return s.replace("`", "``");
        }

        private static String escapeIdentifier(String s) {
            return "`" + escapeBackticks(s) + "`";
        }
    }
}
