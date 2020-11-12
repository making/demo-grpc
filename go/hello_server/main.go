package main

import (
	"context"
	"fmt"
	"github.com/making/demo-grpc/go/hello_client/demo"
	"google.golang.org/grpc"
	"log"
	"net"
)

type helloService struct {
	demo.UnimplementedHelloServiceServer
}

func (s helloService) SayHello(ctx context.Context, request *demo.HelloRequest) (*demo.HelloResponse, error) {
	log.Printf("Received: %v", request.GetGreeting())
	return &demo.HelloResponse{Reply: "Hello " + request.GetGreeting()}, nil
}

func (s helloService) LotsOfReplies(request *demo.HelloRequest, stream demo.HelloService_LotsOfRepliesServer) error {
	for i := 0; i < 10; i++ {
		err := stream.Send(&demo.HelloResponse{Reply: fmt.Sprintf("[%05d] Hello %s", i, request.GetGreeting())})
		if err != nil {
			return err
		}
	}
	return nil
}

func main() {
	lis, err := net.Listen("tcp", ":50051")
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()
	demo.RegisterHelloServiceServer(s, &helloService{})
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
