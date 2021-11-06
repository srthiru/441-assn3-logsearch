package rpcservice.protos.searcher

object SearcherGrpc {
  val METHOD_SEARCH_LOG: _root_.io.grpc.MethodDescriptor[rpcservice.protos.searcher.SearchRequest, rpcservice.protos.searcher.SearchReply] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("rpcservice.protos.Searcher", "searchLog"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(_root_.scalapb.grpc.Marshaller.forMessage[rpcservice.protos.searcher.SearchRequest])
      .setResponseMarshaller(_root_.scalapb.grpc.Marshaller.forMessage[rpcservice.protos.searcher.SearchReply])
      .setSchemaDescriptor(_root_.scalapb.grpc.ConcreteProtoMethodDescriptorSupplier.fromMethodDescriptor(rpcservice.protos.searcher.SearcherProto.javaDescriptor.getServices().get(0).getMethods().get(0)))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("rpcservice.protos.Searcher")
      .setSchemaDescriptor(new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(rpcservice.protos.searcher.SearcherProto.javaDescriptor))
      .addMethod(METHOD_SEARCH_LOG)
      .build()
  
  /** The greeting service definition.
    */
  trait Searcher extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = Searcher
    /** Sends a greeting
      */
    def searchLog(request: rpcservice.protos.searcher.SearchRequest): scala.concurrent.Future[rpcservice.protos.searcher.SearchReply]
  }
  
  object Searcher extends _root_.scalapb.grpc.ServiceCompanion[Searcher] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[Searcher] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = rpcservice.protos.searcher.SearcherProto.javaDescriptor.getServices().get(0)
    def scalaDescriptor: _root_.scalapb.descriptors.ServiceDescriptor = rpcservice.protos.searcher.SearcherProto.scalaDescriptor.services(0)
    def bindService(serviceImpl: Searcher, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
      _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
      .addMethod(
        METHOD_SEARCH_LOG,
        _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[rpcservice.protos.searcher.SearchRequest, rpcservice.protos.searcher.SearchReply] {
          override def invoke(request: rpcservice.protos.searcher.SearchRequest, observer: _root_.io.grpc.stub.StreamObserver[rpcservice.protos.searcher.SearchReply]): Unit =
            serviceImpl.searchLog(request).onComplete(scalapb.grpc.Grpc.completeObserver(observer))(
              executionContext)
        }))
      .build()
  }
  
  /** The greeting service definition.
    */
  trait SearcherBlockingClient {
    def serviceCompanion = Searcher
    /** Sends a greeting
      */
    def searchLog(request: rpcservice.protos.searcher.SearchRequest): rpcservice.protos.searcher.SearchReply
  }
  
  class SearcherBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[SearcherBlockingStub](channel, options) with SearcherBlockingClient {
    /** Sends a greeting
      */
    override def searchLog(request: rpcservice.protos.searcher.SearchRequest): rpcservice.protos.searcher.SearchReply = {
      _root_.scalapb.grpc.ClientCalls.blockingUnaryCall(channel, METHOD_SEARCH_LOG, options, request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): SearcherBlockingStub = new SearcherBlockingStub(channel, options)
  }
  
  class SearcherStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[SearcherStub](channel, options) with Searcher {
    /** Sends a greeting
      */
    override def searchLog(request: rpcservice.protos.searcher.SearchRequest): scala.concurrent.Future[rpcservice.protos.searcher.SearchReply] = {
      _root_.scalapb.grpc.ClientCalls.asyncUnaryCall(channel, METHOD_SEARCH_LOG, options, request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): SearcherStub = new SearcherStub(channel, options)
  }
  
  def bindService(serviceImpl: Searcher, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition = Searcher.bindService(serviceImpl, executionContext)
  
  def blockingStub(channel: _root_.io.grpc.Channel): SearcherBlockingStub = new SearcherBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): SearcherStub = new SearcherStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = rpcservice.protos.searcher.SearcherProto.javaDescriptor.getServices().get(0)
  
}