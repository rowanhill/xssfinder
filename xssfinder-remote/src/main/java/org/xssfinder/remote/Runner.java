/**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.xssfinder.remote;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {

  public interface Iface {

    /**
     * Gets definitions of all page objects in the given namespace
     * 
     * @param namespaceIdentifier The namespace in which to search for page definitions
     * @return set<PageDefinition> A set of PageDefinitions for all page objects in the namespace
     * 
     * @param namespaceIdentifier
     */
    public Set<PageDefinition> getPageDefinitions(String namespaceIdentifier) throws org.apache.thrift.TException;

  }

  public interface AsyncIface {

    public void getPageDefinitions(String namespaceIdentifier, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.getPageDefinitions_call> resultHandler) throws org.apache.thrift.TException;

  }

  public static class Client extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
      public Factory() {}
      public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
        return new Client(prot);
      }
      public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        return new Client(iprot, oprot);
      }
    }

    public Client(org.apache.thrift.protocol.TProtocol prot)
    {
      super(prot, prot);
    }

    public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
      super(iprot, oprot);
    }

    public Set<PageDefinition> getPageDefinitions(String namespaceIdentifier) throws org.apache.thrift.TException
    {
      send_getPageDefinitions(namespaceIdentifier);
      return recv_getPageDefinitions();
    }

    public void send_getPageDefinitions(String namespaceIdentifier) throws org.apache.thrift.TException
    {
      getPageDefinitions_args args = new getPageDefinitions_args();
      args.setNamespaceIdentifier(namespaceIdentifier);
      sendBase("getPageDefinitions", args);
    }

    public Set<PageDefinition> recv_getPageDefinitions() throws org.apache.thrift.TException
    {
      getPageDefinitions_result result = new getPageDefinitions_result();
      receiveBase(result, "getPageDefinitions");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "getPageDefinitions failed: unknown result");
    }

  }
  public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
    public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;
      public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
        this.clientManager = clientManager;
        this.protocolFactory = protocolFactory;
      }
      public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
        return new AsyncClient(protocolFactory, clientManager, transport);
      }
    }

    public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
      super(protocolFactory, clientManager, transport);
    }

    public void getPageDefinitions(String namespaceIdentifier, org.apache.thrift.async.AsyncMethodCallback<getPageDefinitions_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      getPageDefinitions_call method_call = new getPageDefinitions_call(namespaceIdentifier, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class getPageDefinitions_call extends org.apache.thrift.async.TAsyncMethodCall {
      private String namespaceIdentifier;
      public getPageDefinitions_call(String namespaceIdentifier, org.apache.thrift.async.AsyncMethodCallback<getPageDefinitions_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.namespaceIdentifier = namespaceIdentifier;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("getPageDefinitions", org.apache.thrift.protocol.TMessageType.CALL, 0));
        getPageDefinitions_args args = new getPageDefinitions_args();
        args.setNamespaceIdentifier(namespaceIdentifier);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public Set<PageDefinition> getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_getPageDefinitions();
      }
    }

  }

  public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());
    public Processor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
    }

    protected Processor(I iface, Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends Iface> Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> getProcessMap(Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      processMap.put("getPageDefinitions", new getPageDefinitions());
      return processMap;
    }

    private static class getPageDefinitions<I extends Iface> extends org.apache.thrift.ProcessFunction<I, getPageDefinitions_args> {
      public getPageDefinitions() {
        super("getPageDefinitions");
      }

      protected getPageDefinitions_args getEmptyArgsInstance() {
        return new getPageDefinitions_args();
      }

      protected getPageDefinitions_result getResult(I iface, getPageDefinitions_args args) throws org.apache.thrift.TException {
        getPageDefinitions_result result = new getPageDefinitions_result();
        result.success = iface.getPageDefinitions(args.namespaceIdentifier);
        return result;
      }
    }

  }

  public static class getPageDefinitions_args implements org.apache.thrift.TBase<getPageDefinitions_args, getPageDefinitions_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("getPageDefinitions_args");

    private static final org.apache.thrift.protocol.TField NAMESPACE_IDENTIFIER_FIELD_DESC = new org.apache.thrift.protocol.TField("namespaceIdentifier", org.apache.thrift.protocol.TType.STRING, (short)1);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new getPageDefinitions_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new getPageDefinitions_argsTupleSchemeFactory());
    }

    public String namespaceIdentifier; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      NAMESPACE_IDENTIFIER((short)1, "namespaceIdentifier");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // NAMESPACE_IDENTIFIER
            return NAMESPACE_IDENTIFIER;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.NAMESPACE_IDENTIFIER, new org.apache.thrift.meta_data.FieldMetaData("namespaceIdentifier", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(getPageDefinitions_args.class, metaDataMap);
    }

    public getPageDefinitions_args() {
    }

    public getPageDefinitions_args(
      String namespaceIdentifier)
    {
      this();
      this.namespaceIdentifier = namespaceIdentifier;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public getPageDefinitions_args(getPageDefinitions_args other) {
      if (other.isSetNamespaceIdentifier()) {
        this.namespaceIdentifier = other.namespaceIdentifier;
      }
    }

    public getPageDefinitions_args deepCopy() {
      return new getPageDefinitions_args(this);
    }

    @Override
    public void clear() {
      this.namespaceIdentifier = null;
    }

    public String getNamespaceIdentifier() {
      return this.namespaceIdentifier;
    }

    public getPageDefinitions_args setNamespaceIdentifier(String namespaceIdentifier) {
      this.namespaceIdentifier = namespaceIdentifier;
      return this;
    }

    public void unsetNamespaceIdentifier() {
      this.namespaceIdentifier = null;
    }

    /** Returns true if field namespaceIdentifier is set (has been assigned a value) and false otherwise */
    public boolean isSetNamespaceIdentifier() {
      return this.namespaceIdentifier != null;
    }

    public void setNamespaceIdentifierIsSet(boolean value) {
      if (!value) {
        this.namespaceIdentifier = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case NAMESPACE_IDENTIFIER:
        if (value == null) {
          unsetNamespaceIdentifier();
        } else {
          setNamespaceIdentifier((String)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case NAMESPACE_IDENTIFIER:
        return getNamespaceIdentifier();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case NAMESPACE_IDENTIFIER:
        return isSetNamespaceIdentifier();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof getPageDefinitions_args)
        return this.equals((getPageDefinitions_args)that);
      return false;
    }

    public boolean equals(getPageDefinitions_args that) {
      if (that == null)
        return false;

      boolean this_present_namespaceIdentifier = true && this.isSetNamespaceIdentifier();
      boolean that_present_namespaceIdentifier = true && that.isSetNamespaceIdentifier();
      if (this_present_namespaceIdentifier || that_present_namespaceIdentifier) {
        if (!(this_present_namespaceIdentifier && that_present_namespaceIdentifier))
          return false;
        if (!this.namespaceIdentifier.equals(that.namespaceIdentifier))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(getPageDefinitions_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      getPageDefinitions_args typedOther = (getPageDefinitions_args)other;

      lastComparison = Boolean.valueOf(isSetNamespaceIdentifier()).compareTo(typedOther.isSetNamespaceIdentifier());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetNamespaceIdentifier()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.namespaceIdentifier, typedOther.namespaceIdentifier);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("getPageDefinitions_args(");
      boolean first = true;

      sb.append("namespaceIdentifier:");
      if (this.namespaceIdentifier == null) {
        sb.append("null");
      } else {
        sb.append(this.namespaceIdentifier);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class getPageDefinitions_argsStandardSchemeFactory implements SchemeFactory {
      public getPageDefinitions_argsStandardScheme getScheme() {
        return new getPageDefinitions_argsStandardScheme();
      }
    }

    private static class getPageDefinitions_argsStandardScheme extends StandardScheme<getPageDefinitions_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, getPageDefinitions_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // NAMESPACE_IDENTIFIER
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.namespaceIdentifier = iprot.readString();
                struct.setNamespaceIdentifierIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, getPageDefinitions_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.namespaceIdentifier != null) {
          oprot.writeFieldBegin(NAMESPACE_IDENTIFIER_FIELD_DESC);
          oprot.writeString(struct.namespaceIdentifier);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class getPageDefinitions_argsTupleSchemeFactory implements SchemeFactory {
      public getPageDefinitions_argsTupleScheme getScheme() {
        return new getPageDefinitions_argsTupleScheme();
      }
    }

    private static class getPageDefinitions_argsTupleScheme extends TupleScheme<getPageDefinitions_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, getPageDefinitions_args struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetNamespaceIdentifier()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetNamespaceIdentifier()) {
          oprot.writeString(struct.namespaceIdentifier);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, getPageDefinitions_args struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.namespaceIdentifier = iprot.readString();
          struct.setNamespaceIdentifierIsSet(true);
        }
      }
    }

  }

  public static class getPageDefinitions_result implements org.apache.thrift.TBase<getPageDefinitions_result, getPageDefinitions_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("getPageDefinitions_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.SET, (short)0);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new getPageDefinitions_resultStandardSchemeFactory());
      schemes.put(TupleScheme.class, new getPageDefinitions_resultTupleSchemeFactory());
    }

    public Set<PageDefinition> success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 0: // SUCCESS
            return SUCCESS;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
              new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, PageDefinition.class))));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(getPageDefinitions_result.class, metaDataMap);
    }

    public getPageDefinitions_result() {
    }

    public getPageDefinitions_result(
      Set<PageDefinition> success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public getPageDefinitions_result(getPageDefinitions_result other) {
      if (other.isSetSuccess()) {
        Set<PageDefinition> __this__success = new HashSet<PageDefinition>();
        for (PageDefinition other_element : other.success) {
          __this__success.add(new PageDefinition(other_element));
        }
        this.success = __this__success;
      }
    }

    public getPageDefinitions_result deepCopy() {
      return new getPageDefinitions_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public int getSuccessSize() {
      return (this.success == null) ? 0 : this.success.size();
    }

    public java.util.Iterator<PageDefinition> getSuccessIterator() {
      return (this.success == null) ? null : this.success.iterator();
    }

    public void addToSuccess(PageDefinition elem) {
      if (this.success == null) {
        this.success = new HashSet<PageDefinition>();
      }
      this.success.add(elem);
    }

    public Set<PageDefinition> getSuccess() {
      return this.success;
    }

    public getPageDefinitions_result setSuccess(Set<PageDefinition> success) {
      this.success = success;
      return this;
    }

    public void unsetSuccess() {
      this.success = null;
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return this.success != null;
    }

    public void setSuccessIsSet(boolean value) {
      if (!value) {
        this.success = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((Set<PageDefinition>)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof getPageDefinitions_result)
        return this.equals((getPageDefinitions_result)that);
      return false;
    }

    public boolean equals(getPageDefinitions_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true && this.isSetSuccess();
      boolean that_present_success = true && that.isSetSuccess();
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (!this.success.equals(that.success))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(getPageDefinitions_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      getPageDefinitions_result typedOther = (getPageDefinitions_result)other;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(typedOther.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, typedOther.success);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
      }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("getPageDefinitions_result(");
      boolean first = true;

      sb.append("success:");
      if (this.success == null) {
        sb.append("null");
      } else {
        sb.append(this.success);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class getPageDefinitions_resultStandardSchemeFactory implements SchemeFactory {
      public getPageDefinitions_resultStandardScheme getScheme() {
        return new getPageDefinitions_resultStandardScheme();
      }
    }

    private static class getPageDefinitions_resultStandardScheme extends StandardScheme<getPageDefinitions_result> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, getPageDefinitions_result struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 0: // SUCCESS
              if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
                {
                  org.apache.thrift.protocol.TSet _set8 = iprot.readSetBegin();
                  struct.success = new HashSet<PageDefinition>(2*_set8.size);
                  for (int _i9 = 0; _i9 < _set8.size; ++_i9)
                  {
                    PageDefinition _elem10; // required
                    _elem10 = new PageDefinition();
                    _elem10.read(iprot);
                    struct.success.add(_elem10);
                  }
                  iprot.readSetEnd();
                }
                struct.setSuccessIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, getPageDefinitions_result struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.success != null) {
          oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
          {
            oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRUCT, struct.success.size()));
            for (PageDefinition _iter11 : struct.success)
            {
              _iter11.write(oprot);
            }
            oprot.writeSetEnd();
          }
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class getPageDefinitions_resultTupleSchemeFactory implements SchemeFactory {
      public getPageDefinitions_resultTupleScheme getScheme() {
        return new getPageDefinitions_resultTupleScheme();
      }
    }

    private static class getPageDefinitions_resultTupleScheme extends TupleScheme<getPageDefinitions_result> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, getPageDefinitions_result struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSuccess()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetSuccess()) {
          {
            oprot.writeI32(struct.success.size());
            for (PageDefinition _iter12 : struct.success)
            {
              _iter12.write(oprot);
            }
          }
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, getPageDefinitions_result struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          {
            org.apache.thrift.protocol.TSet _set13 = new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
            struct.success = new HashSet<PageDefinition>(2*_set13.size);
            for (int _i14 = 0; _i14 < _set13.size; ++_i14)
            {
              PageDefinition _elem15; // required
              _elem15 = new PageDefinition();
              _elem15.read(iprot);
              struct.success.add(_elem15);
            }
          }
          struct.setSuccessIsSet(true);
        }
      }
    }

  }

}
