// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/protobuf/empty.proto

#ifndef PROTOBUF_google_2fprotobuf_2fempty_2eproto__INCLUDED
#define PROTOBUF_google_2fprotobuf_2fempty_2eproto__INCLUDED

#include <string>

#include <google/protobuf/stubs/common.h>

#if GOOGLE_PROTOBUF_VERSION < 3000000
#error This file was generated by a newer version of protoc which is
#error incompatible with your Protocol Buffer headers.  Please update
#error your headers.
#endif
#if 3000000 < GOOGLE_PROTOBUF_MIN_PROTOC_VERSION
#error This file was generated by an older version of protoc which is
#error incompatible with your Protocol Buffer headers.  Please
#error regenerate this file with a newer version of protoc.
#endif

#include <google/protobuf/arena.h>
#include <google/protobuf/arenastring.h>
#include <google/protobuf/generated_message_util.h>
#include <google/protobuf/metadata.h>
#include <google/protobuf/message.h>
#include <google/protobuf/repeated_field.h>
#include <google/protobuf/extension_set.h>
#include <google/protobuf/unknown_field_set.h>
// @@protoc_insertion_point(includes)

namespace google {
namespace protobuf {

// Internal implementation detail -- do not call these.
void LIBPROTOBUF_EXPORT protobuf_AddDesc_google_2fprotobuf_2fempty_2eproto();
void protobuf_AssignDesc_google_2fprotobuf_2fempty_2eproto();
void protobuf_ShutdownFile_google_2fprotobuf_2fempty_2eproto();

class Empty;

// ===================================================================

class LIBPROTOBUF_EXPORT Empty : public ::google::protobuf::Message {
 public:
  Empty();
  virtual ~Empty();

  Empty(const Empty& from);

  inline Empty& operator=(const Empty& from) {
    CopyFrom(from);
    return *this;
  }

  inline ::google::protobuf::Arena* GetArena() const { return GetArenaNoVirtual(); }
  inline void* GetMaybeArenaPointer() const {
    return MaybeArenaPtr();
  }
  static const ::google::protobuf::Descriptor* descriptor();
  static const Empty& default_instance();

  void UnsafeArenaSwap(Empty* other);
  void Swap(Empty* other);

  // implements Message ----------------------------------------------

  inline Empty* New() const { return New(NULL); }

  Empty* New(::google::protobuf::Arena* arena) const;
  void CopyFrom(const ::google::protobuf::Message& from);
  void MergeFrom(const ::google::protobuf::Message& from);
  void CopyFrom(const Empty& from);
  void MergeFrom(const Empty& from);
  void Clear();
  bool IsInitialized() const;

  int ByteSize() const;
  bool MergePartialFromCodedStream(
      ::google::protobuf::io::CodedInputStream* input);
  void SerializeWithCachedSizes(
      ::google::protobuf::io::CodedOutputStream* output) const;
  ::google::protobuf::uint8* SerializeWithCachedSizesToArray(::google::protobuf::uint8* output) const;
  int GetCachedSize() const { return _cached_size_; }
  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const;
  void InternalSwap(Empty* other);
  protected:
  explicit Empty(::google::protobuf::Arena* arena);
  private:
  static void ArenaDtor(void* object);
  inline void RegisterArenaDtor(::google::protobuf::Arena* arena);
  private:
  inline ::google::protobuf::Arena* GetArenaNoVirtual() const {
    return _internal_metadata_.arena();
  }
  inline void* MaybeArenaPtr() const {
    return _internal_metadata_.raw_arena_ptr();
  }
  public:

  ::google::protobuf::Metadata GetMetadata() const;

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  // @@protoc_insertion_point(class_scope:google.protobuf.Empty)
 private:

  ::google::protobuf::internal::InternalMetadataWithArena _internal_metadata_;
  friend class ::google::protobuf::Arena;
  typedef void InternalArenaConstructable_;
  typedef void DestructorSkippable_;
  bool _is_default_instance_;
  mutable int _cached_size_;
  friend void LIBPROTOBUF_EXPORT protobuf_AddDesc_google_2fprotobuf_2fempty_2eproto();
  friend void protobuf_AssignDesc_google_2fprotobuf_2fempty_2eproto();
  friend void protobuf_ShutdownFile_google_2fprotobuf_2fempty_2eproto();

  void InitAsDefaultInstance();
  static Empty* default_instance_;
};
// ===================================================================


// ===================================================================

#if !PROTOBUF_INLINE_NOT_IN_HEADERS
// Empty

#endif  // !PROTOBUF_INLINE_NOT_IN_HEADERS

// @@protoc_insertion_point(namespace_scope)

}  // namespace protobuf
}  // namespace google

// @@protoc_insertion_point(global_scope)

#endif  // PROTOBUF_google_2fprotobuf_2fempty_2eproto__INCLUDED
